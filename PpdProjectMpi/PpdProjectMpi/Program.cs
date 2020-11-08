using MPI;
using System.Threading;
using System;
using System.Collections.Generic;
using System.Linq;


namespace PpdProjectMpi
{
	class Program
	{
		static int N = 5;
		static int T;

		static bool pred(List<int> v)
		{
			return v[0] % 2 == 1;
		}

		static int next(List<int> current, int currT, int me)
		{
			
			int vectorSum = current.Sum();
			if(vectorSum == N)
			{
				if (pred(current))
				{
					return 1;
				}
				return 0;
			} else if (vectorSum > N)
			{
				return 0;
			}
				
			int last = N + 1;
			if(current.Count!= 0){
				last = current[current.Count - 1];
			}
			int cnt = 0;
			if(currT >= T)
			{
				for (int i = last; i >= 1; i--)
				{
					current.Add(i);
					cnt = cnt + next(current, currT, me);
					current.RemoveAt(current.Count - 1);
				}
				return cnt;
			} else
			{
				int dest = currT;
				currT++;
				Communicator.world.Send(current, dest, 0);
				Communicator.world.Send(currT, dest, 1);
				Communicator.world.Send(me, dest, 2);
				for (int i = last-1; i >= 1; i = i - 2)
				{
					current.Add(i);
					cnt = cnt + next(current, currT, me);
					current.RemoveAt(current.Count - 1);
				}
				cnt = cnt + Communicator.world.Receive<int>(dest, 3);
				return cnt;
			}
		}

		static void nextWorker(int me)
		{
			int cnt = 0;
			List<int> current = Communicator.world.Receive<List<int>>(MPI.Communicator.anySource, 0);
			int currT = Communicator.world.Receive<int>(MPI.Communicator.anySource, 1);
			int dest = Communicator.world.Receive<int>(MPI.Communicator.anySource, 2);

			Console.WriteLine("I am worker " + me.ToString() + "and received everything");

			int last = N + 1;
			if (current.Count != 0)
			{
				last = current[current.Count - 1];
			}
			for (int i = last; i >= 1; i = i - 2)
			{
				current.Add(i);
				cnt = cnt + next(current, currT, me);
				current.RemoveAt(current.Count - 1);
			}

			Communicator.world.Send(cnt, dest, 3);
		}

		static void Main(string[] args)
		{
			using (new MPI.Environment(ref args, Threading.Multiple))
			{
				T = Communicator.world.Size;
				if (Communicator.world.Rank == 0)
				{
					List<int> start = new List<int>();
					int cnt = next(start, 1, Communicator.world.Rank);
					Console.WriteLine("result is " + cnt.ToString());
				}
				else
				{
					nextWorker(Communicator.world.Rank);
				}
			}

			

		}
	}
}
