using HTTPreq.domain;
using HTTPreq.parser;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace HTTPreq.impl
{
	class TaskWrap
	{
		private static List<string> serversList;

		public static void startApp(List<string> links)
		{
			serversList = links;

			List<Task> tasks = new List<Task>();

			for (int i = 0; i < serversList.Count; i++)
			{
				tasks.Add(Task.Factory.StartNew(doStart, i));
			}

			Task.WaitAll(tasks.ToArray());
		}

		private static void doStart(object idObject)
		{
			int id = (int)idObject;

			StartClient(serversList[id], id);
		}
		

		private static void StartClient(string host, int id)
		{
			IPHostEntry ipHostInfo = Dns.GetHostEntry(host.Split('/')[0]);
			IPAddress ipAddress = ipHostInfo.AddressList[0];
			IPEndPoint serverIpAddress = new IPEndPoint(ipAddress, Parser.HTTP_PORT);

			Socket client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

			MyInfoWrapper myInfoWrapper = new MyInfoWrapper
			{
				clientSocket = client,
				hostname = host.Split('/')[0],
				requestPath = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/",
				serverIpAddress = serverIpAddress,
				id = id
			};

			ConnectWrapper(myInfoWrapper).Wait();

			SendWrapper(myInfoWrapper, Parser.getRequestString(myInfoWrapper.hostname, myInfoWrapper.requestPath)).Wait();

			ReceiveWrapper(myInfoWrapper).Wait();

			Console.WriteLine(
							"<<< Thread with id: {0} >>> Received as response {1} characters ({2} chars in header, {3} chars in body)",
							myInfoWrapper.id,
							myInfoWrapper.receivedCharacters.Length,
							myInfoWrapper.receivedCharacters.Length - Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()),
							Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()));

			myInfoWrapper.clientSocket.Shutdown(SocketShutdown.Both);
			myInfoWrapper.clientSocket.Close();
		}

		private static Task ConnectWrapper(MyInfoWrapper myInfoWrapper)
		{
			myInfoWrapper.clientSocket.BeginConnect(myInfoWrapper.serverIpAddress, ConnectCallback, myInfoWrapper);

			return Task.FromResult(myInfoWrapper.connectFinishedMRE.WaitOne());
		}

		private static void ConnectCallback(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;
		    string hostname = myInfoWrapper.hostname;

			clientSocket.EndConnect(ar);

			Console.WriteLine("<<< Thread with id: {0} >>> connected to server = {1} , ip = {2}", clientId, hostname, clientSocket.RemoteEndPoint);

			// signal that the connection has been made
			myInfoWrapper.connectFinishedMRE.Set();
		}

		private static Task SendWrapper(MyInfoWrapper myInfoWrapper, string data)
		{
			byte[] byteData = Encoding.ASCII.GetBytes(data);

			myInfoWrapper.clientSocket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, myInfoWrapper);

			return Task.FromResult(myInfoWrapper.sendFinishedMRE.WaitOne());
		}

		private static void SendCallback(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;

			int bytesSent = clientSocket.EndSend(ar);
			Console.WriteLine("<<< Thread with id: {0} >>> sent to the server a HTTP request of {1} bytes", clientId, bytesSent);

			// signal that all bytes have been sent
			myInfoWrapper.sendFinishedMRE.Set();
		}

		private static Task ReceiveWrapper(MyInfoWrapper myInfoWrapper)
		{
			myInfoWrapper.clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, ReceiveCallback, myInfoWrapper);

			return Task.FromResult(myInfoWrapper.receiveFinishedMRE.WaitOne());
		}

		private static void ReceiveCallback(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;

			try
			{
				int bytesRead = clientSocket.EndReceive(ar);

				myInfoWrapper.receivedCharacters.Append(Encoding.ASCII.GetString(myInfoWrapper.receiveBuffer, 0, bytesRead));

				Console.WriteLine(myInfoWrapper.receivedCharacters);

				if (!Parser.responseHeaderFullyObtained(myInfoWrapper.receivedCharacters.ToString()))
				{
					clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, ReceiveCallback, myInfoWrapper);
				}
				else
				{
					string responseBody = Parser.getResponseBody(myInfoWrapper.receivedCharacters.ToString());

					if (responseBody.Length < Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()))
					{
						clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, ReceiveCallback, myInfoWrapper);
					}
					else
					{
						// signal that all bytes have been received  
						myInfoWrapper.receiveFinishedMRE.Set();
					}
				}
			}
			catch (Exception e)
			{
				Console.WriteLine(e.ToString());
			}
		}
	}
}
