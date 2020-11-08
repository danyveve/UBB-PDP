using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PpdProjectMpi
{
	[Serializable]
	class ImagePixels
	{
		//private Dictionary<Tuple<int, int>, byte> _matrixPixelsValue = new Dictionary<Tuple<int, int>, byte>();
		private Dictionary<Tuple<int, int>, Color> _matrixPixelsValue = new Dictionary<Tuple<int, int>, Color>();
		private int _width = -1;
		private int _height = -1;

		public ImagePixels(string filepath)
		{
			Bitmap bmp = new Bitmap(filepath);

			this._height = bmp.Height;
			this._width = bmp.Width;
			for (int i = 0; i < bmp.Width; i++)
			{
				for (int j = 0; j < bmp.Height; j++)
				{
					_matrixPixelsValue[Tuple.Create<int, int>(i, j)] = bmp.GetPixel(i, j); //we assume grayscale => we only keep one color
				}
			}

			bmp.Dispose();
		}

		public ImagePixels(int width, int height)
		{
			this._height = height;
			this._width = width;
		}


		public int Width
		{
			get => _width;
			set => _width = value;
		}

		public int Height
		{
			get => _height;
			set => _height = value;
		}

		public Color getPixelValue(int xPixel, int yPixel)
		{
			return _matrixPixelsValue[Tuple.Create<int, int>(xPixel, yPixel)];
		}

		public void setPixelValue(int xPixel, int yPixel, Color pixelValue)
		{
			_matrixPixelsValue[Tuple.Create<int, int>(xPixel, yPixel)] = pixelValue;
		}


		public override string ToString()
		{
			StringBuilder message = new StringBuilder("", 10000000);
			for (int i = 0; i < this.Width; i++)
			{
				for (int j = 0; j < this.Height; j++)
				{
					message.Append(_matrixPixelsValue[Tuple.Create<int, int>(i, j)] + " ");
				}
				message.Append("\n");
			}
			return message.ToString();
		}
	}
}
