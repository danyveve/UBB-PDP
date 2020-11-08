using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MPI;


namespace PpdProjectMpi
{
	class Controller
	{
		string filePath = "";
		string outputPath = "";
		ImagePixels originalImagePixels = null;
		int noTasks = 0;

		public Controller(string filePath, string outputPath)
		{
			this.filePath = filePath;
			this.outputPath = outputPath;
			this.noTasks = Communicator.world.Size - 1;

			originalImagePixels = new ImagePixels(this.filePath);
		}

		

		internal void startSending()
		{
			Console.WriteLine("Image conversion began");
			var watch = System.Diagnostics.Stopwatch.StartNew();
			// the code that you want to measure comes here
			this.sendToChildren();
			List < Tuple < Tuple<int, int>, Color >> greyedOutBits = this.mainStartReceiveFromChildren();
			this.saveFinalImageToPath(greyedOutBits);
			// end of measuring
			watch.Stop();
			var elapsedMs = watch.ElapsedMilliseconds;
			Console.WriteLine("Ellapsed time in miliseconds is: " + elapsedMs);
		}

		private void saveFinalImageToPath(List<Tuple<Tuple<int, int>, Color>> greyedOutBits)
		{
			Bitmap outputImage = new Bitmap(originalImagePixels.Width, originalImagePixels.Height);
			foreach (var item in greyedOutBits)
			{
				Tuple<int, int> matrixPosition = item.Item1;
				Color pixelNewColor = item.Item2;

				outputImage.SetPixel(matrixPosition.Item1, matrixPosition.Item2, pixelNewColor);
			}


			outputImage.Save(outputPath);
		}

		internal void childrenStartReceiving()
		{
			//child receives indexes from parent
			Tuple<int, int> indexes = Communicator.world.Receive<Tuple<int, int>>(0, 0);
			//child greys out his slice of the image
			List<Tuple<Tuple<int, int>, Color>> partialResult = this.taskFunction(indexes.Item1, indexes.Item2);
			//child send his slice back to the parent
			Communicator.world.Send(partialResult, 0, 1);
		}


		private void sendToChildren()
		{
			int noOfOperations = originalImagePixels.Width * originalImagePixels.Height;
			int noTasksAvailable = this.noTasks;
			int currentOperationStart = 1;
			int currentTotalNoOperationsLeft = noOfOperations;

			for (int i = 1; i <= this.noTasks; i += 1)
			{
				int noOperationsPerTask = (int)Math.Ceiling((currentTotalNoOperationsLeft * 1.0) / (noTasksAvailable * 1.0));
				Console.WriteLine("Machine rank : " + i + " has total operations: " + noOperationsPerTask);
				int localCurrentOperationStart = currentOperationStart;

				Tuple<int, int> indexes = new Tuple<int, int>(localCurrentOperationStart, localCurrentOperationStart + noOperationsPerTask - 1);
				Communicator.world.Send(indexes, i, 0);

				noTasksAvailable -= 1;
				currentOperationStart += noOperationsPerTask;
				currentTotalNoOperationsLeft -= noOperationsPerTask;
			}
		}

		private List<Tuple<Tuple<int, int>, Color>> mainStartReceiveFromChildren()
		{
			List<Tuple<Tuple<int, int>, Color>> greyedOutBits = new List<Tuple<Tuple<int, int>, Color>>();
			//main starts receiving from each children
			for (int i = 1; i <= this.noTasks; i +=1)
			{
				List<Tuple<Tuple<int, int>, Color>> partialResult = Communicator.world.Receive<List<Tuple<Tuple<int, int>, Color>>>(i, 1);
				greyedOutBits.AddRange(partialResult);
			}

			return greyedOutBits;
		}

		static Color computeNewPixelAfterGrayScaling(ImagePixels imagePixels, int xPixel, int yPixel)
		{
			//yPixel is for width and xPixel is for height => they must be reversed here in the call
			//Console.WriteLine("x = {0} and y = {1}", xPixel, yPixel);
			Color imagePixelValue = imagePixels.getPixelValue(yPixel, xPixel);

			float redFLoat = (float)imagePixelValue.R / 255.0f;
			float greenFloat = (float)imagePixelValue.G / 255.0f;
			float blueFloat = (float)imagePixelValue.B / 255.0f;


			float newGrayScaleColorValue = 0.21f * redFLoat + 0.72f * greenFloat + 0.07f * blueFloat;
			int newColorValue = (int)(255 * newGrayScaleColorValue);
			Color grayColor = Color.FromArgb(newColorValue, newColorValue, newColorValue);


			return grayColor;
		}

		static Tuple<int, int> generateMatrixPositionFromIndex(ImagePixels imagePixels, int index)
		{
			int row = (int)Math.Ceiling((index * 1.0) / (1.0 * imagePixels.Width));
			int col = index - imagePixels.Width * (row - 1);
			return Tuple.Create<int, int>(row - 1, col - 1);
		}

		static bool isPositionValidInMatrix(ImagePixels imagePixels, int x, int y)
		{
			if (x < 0 || y < 0 || x >= imagePixels.Height || y >= imagePixels.Width)
				return false;
			return true;
		}

		private List<Tuple<Tuple<int, int>, Color>> taskFunction(int startingIndex, int endingIndex)
		{
			List<Tuple<Tuple<int, int>, Color>> resultList = new List<Tuple<Tuple<int, int>, Color>>();

			for (int i = startingIndex; i <= endingIndex; i += 1)
			{
				Tuple<int, int> matrixPosition = Controller.generateMatrixPositionFromIndex(this.originalImagePixels, i);

				if (Controller.isPositionValidInMatrix(this.originalImagePixels, matrixPosition.Item1, matrixPosition.Item2))
				{
					Color newColor = Controller.computeNewPixelAfterGrayScaling(
						this.originalImagePixels,
						matrixPosition.Item1,
						matrixPosition.Item2);

					Tuple<int, int> matrixPositionByWidth = Tuple.Create<int, int>(matrixPosition.Item2, matrixPosition.Item1);
					var resultItem = Tuple.Create<Tuple<int, int>, Color>(matrixPositionByWidth, newColor);
					resultList.Add(resultItem);
				}
			}

			return resultList;
		}
	}
}
