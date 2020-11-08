using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HTTPreq.parser
{
	class Parser
	{
		public static readonly int HTTP_PORT = 80;
		public static string getResponseBody(string responseContent)
		{
			var responseParts = responseContent.Split(new[] { "\r\n\r\n" }, StringSplitOptions.RemoveEmptyEntries);

			return responseParts.Length > 1 ? responseParts[1] : "";
		}

		public static bool responseHeaderFullyObtained(string responseContent)
		{
			return responseContent.Contains("\r\n\r\n");
		}

		public static int getContentLength(string responseContent)
		{
			var contentLength = 0;
			var responseLines = responseContent.Split('\r', '\n');

			foreach (var responseLine in responseLines)
			{
				//header_name : header_value format
				var headerDetails = responseLine.Split(':');

				if (headerDetails[0].CompareTo("Content-Length") == 0)
				{
					contentLength = int.Parse(headerDetails[1]);
				}
			}

			return contentLength;
		}


		public static string getRequestString(string hostname, string endpoint)
		{
			return "GET " + endpoint + " HTTP/1.1\r\n" +
				   "Host: " + hostname + "\r\n" +
				   "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,#1#*;q=0.8\r\n" +
				   // for archived files there will be added the content length file
				   "Accept-Encoding: gzip, deflate\r\n" +
				   "Connection: keep-alive\r\n" +
				   "Content-Length: 0\r\n\r\n";
		}
	}
}
