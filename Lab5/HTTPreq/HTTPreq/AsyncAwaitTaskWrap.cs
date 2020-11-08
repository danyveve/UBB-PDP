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
	class AsyncAwaitTaskWrap
	{
		private static List<string> serversList;

		public static void startApp(List<string> links)
		{
			serversList = links;

			List<Task> tasks = new List<Task>();

			for (int i = 0; i < serversList.Count; i++)
			{
				tasks.Add(Task.Factory.StartNew(startTasks, i));
			}

			Task.WaitAll(tasks.ToArray());
		}

		private static void startTasks(object idObject)
		{
			int id = (int)idObject;

			StartClient(serversList[id], id);
		}



		private static async void StartClient(string server, int id)
		{
			IPHostEntry ipHostInfo = Dns.GetHostEntry(server.Split('/')[0]);
			IPAddress ipAddress = ipHostInfo.AddressList[0];
			IPEndPoint serverIpAddress = new IPEndPoint(ipAddress, Parser.HTTP_PORT);


			Socket clientSocket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);


			MyInfoWrapper myInfoWrapper = new MyInfoWrapper
			{
				clientSocket = clientSocket,
				hostname = server.Split('/')[0],
				requestPath = server.Contains("/") ? server.Substring(server.IndexOf("/")) : "/",
				serverIpAddress = serverIpAddress,
				id = id
			};

			await ConnectWrapper(myInfoWrapper);

			await SendWrapper(myInfoWrapper, Parser.getRequestString(myInfoWrapper.hostname, myInfoWrapper.requestPath));

			await ReceiveWrapper(myInfoWrapper);

			Console.WriteLine(
				"<<< Thread with id: {0} >>> Received as response {1} characters ({2} chars in header, {3} chars in body)",
				id, 
				myInfoWrapper.receivedCharacters.Length, 
				myInfoWrapper.receivedCharacters.Length - Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()),
				Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()));

			clientSocket.Shutdown(SocketShutdown.Both);
			clientSocket.Close();
		}

		private static async Task ConnectWrapper(MyInfoWrapper myInfoWrapper)
		{
			myInfoWrapper.clientSocket.BeginConnect(myInfoWrapper.serverIpAddress, ConnectCallback, myInfoWrapper);

			await Task.FromResult<bool>(myInfoWrapper.connectFinishedMRE.WaitOne());
		}

		private static void ConnectCallback(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;
			string hostname = myInfoWrapper.hostname;

			clientSocket.EndConnect(ar);

			Console.WriteLine("<<< Thread with id: {0} >>> connected to server = {1} , ip = {2}", clientId, hostname, clientSocket.RemoteEndPoint);

			myInfoWrapper.connectFinishedMRE.Set();
		}

		private static async Task SendWrapper(MyInfoWrapper myInfoWrapper, string data)
		{
			byte[] byteData = Encoding.ASCII.GetBytes(data);

			myInfoWrapper.clientSocket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, myInfoWrapper);

			await Task.FromResult<bool>(myInfoWrapper.sendFinishedMRE.WaitOne());
		}

		private static void SendCallback(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;

			int bytesSent = clientSocket.EndSend(ar);
			Console.WriteLine("<<< Thread with id: {0} >>> sent to the server a HTTP request of {1} bytes", clientId, bytesSent);

			myInfoWrapper.sendFinishedMRE.Set();
		}

		private static async Task ReceiveWrapper(MyInfoWrapper myInfoWrapper)
		{
			myInfoWrapper.clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, ReceiveCallback, myInfoWrapper);

			await Task.FromResult<bool>(myInfoWrapper.receiveFinishedMRE.WaitOne());
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
