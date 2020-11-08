
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace HTTPreq.domain
{
	class MyInfoWrapper
	{
		public int id;
		public string hostname;
		public string requestPath;
		public IPEndPoint serverIpAddress;
		public Socket clientSocket = null;
		public const int BUFFER_SIZE = 512;
		public byte[] receiveBuffer = new byte[BUFFER_SIZE];
		public StringBuilder receivedCharacters = new StringBuilder();
		public ManualResetEvent connectFinishedMRE = new ManualResetEvent(false); //unsignaled state
		public ManualResetEvent sendFinishedMRE = new ManualResetEvent(false); //unsignaled state
		public ManualResetEvent receiveFinishedMRE = new ManualResetEvent(false); //unsignaled state

	}
}
