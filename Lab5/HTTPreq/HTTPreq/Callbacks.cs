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
	class Callbacks
	{
		private static List<string> serversList;

		public static void startApp(List<string> links)
		{
			serversList = links;

			for (int i = 0; i < serversList.Count; i++)
			{
				startTasks(i);
			}
		}

		private static void startTasks(object idObject)
		{
			int id = (int)idObject;

			doBeginConnect(serversList[id], id);
		}

		private static void doBeginConnect(string host, int id)
		{
			IPHostEntry ipHostEntry = Dns.GetHostEntry(host.Split('/')[0]);
			IPAddress ipAddress = ipHostEntry.AddressList[0];
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

			myInfoWrapper.clientSocket.BeginConnect(myInfoWrapper.serverIpAddress, doBeginSend, myInfoWrapper);
		}

		private static void doBeginSend(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;
			string hostname = myInfoWrapper.hostname;

			clientSocket.EndConnect(ar);
			Console.WriteLine("<<< Thread with id: {0} >>> connected to server = {1} , ip = {2}", clientId, hostname, clientSocket.RemoteEndPoint);

			byte[] byteData = Encoding.ASCII.GetBytes(Parser.getRequestString(myInfoWrapper.hostname, myInfoWrapper.requestPath));

			myInfoWrapper.clientSocket.BeginSend(byteData, 0, byteData.Length, 0, doBeginReceive, myInfoWrapper);
		}

		private static void doBeginReceive(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;

			int bytesSent = clientSocket.EndSend(ar);
			Console.WriteLine("<<< Thread with id: {0} >>> sent to the server a HTTP request of {1} bytes", clientId, bytesSent);

			myInfoWrapper.clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, doEndReceive, myInfoWrapper);
		}

		private static void doEndReceive(IAsyncResult ar)
		{
			MyInfoWrapper myInfoWrapper = (MyInfoWrapper)ar.AsyncState;
			Socket clientSocket = myInfoWrapper.clientSocket;
			int clientId = myInfoWrapper.id;

			try
			{
				int bytesRead = clientSocket.EndReceive(ar);

				myInfoWrapper.receivedCharacters.Append(Encoding.ASCII.GetString(myInfoWrapper.receiveBuffer, 0, bytesRead));

				//Console.Write(myInfoWrapper.receivedCharacters);

				if (!Parser.responseHeaderFullyObtained(myInfoWrapper.receivedCharacters.ToString()))
				{
					clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, doEndReceive, myInfoWrapper);
				}
				else
				{
					string responseBody = Parser.getResponseBody(myInfoWrapper.receivedCharacters.ToString());

					int contentLengthHeaderValue = Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString());

					if (responseBody.Length < contentLengthHeaderValue)
					{
						clientSocket.BeginReceive(myInfoWrapper.receiveBuffer, 0, MyInfoWrapper.BUFFER_SIZE, 0, doEndReceive, myInfoWrapper);
					}
					else
					{
						Console.WriteLine(
							"<<< Thread with id: {0} >>> Received as response {1} characters ({2} chars in header, {3} chars in body)",
							clientId,
							myInfoWrapper.receivedCharacters.Length,
							myInfoWrapper.receivedCharacters.Length - Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()),
							Parser.getContentLength(myInfoWrapper.receivedCharacters.ToString()));

						clientSocket.Shutdown(SocketShutdown.Both);
						clientSocket.Close();
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
