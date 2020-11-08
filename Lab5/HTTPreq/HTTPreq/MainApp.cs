using HTTPreq.impl;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HTTPreq
{
	class MainApp
	{
		static void Main(string[] args)
		{
			List<string> serversList = new List<String>();
			string line;
			System.IO.StreamReader file = new System.IO.StreamReader("serverslist.txt");
			while ((line = file.ReadLine()) != null)
			{
				serversList.Add(line);
			}
			//Callbacks.startApp(serversList);
			//TaskWrap.startApp(serversList);
			AsyncAwaitTaskWrap.startApp(serversList);
			Console.WriteLine("Press any key to close");
			Console.ReadKey();
		}
	}
}
