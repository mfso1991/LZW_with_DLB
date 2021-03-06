/*************************************************************************
 *	@author: You Zhou
 *  E-mail : yoz13@pitt.edu
 *************************************************************************/

import java.io.*;
import java.util.*; 
 
public class LZWreset
{	
	/** making variable scope as limiting as possible. **/
	public static void compress(int width, int length, int code) throws Exception 
	{ 
		DLB4LZW dlb = new DLB4LZW(new PushbackInputStream(System.in));
		/** TRUE till EOF of the input stream **/
		while(dlb.maxPrefixFound(code++, width, code < 65536))
		{
			if(code == length) 
			{	
				/** increment code size. **/
				if(width < 16)
				{
					width++;
					length <<= 1;
				}
				/** if 16-bit bound was met, reset is performed. **/
				else
				{
					width = 9;
					length = 512;
					code = 257;	
					dlb.init();
				}
			}
		}
    } 

	/** making variable scope as limiting as possible. **/
    public static void expand(int width, int length) 
	{			
        String[] st = new String[65536]; /** safeSize = 2^16. **/
		/** initialize symbol table with all 1-character strings. **/
		for(int i = 0; i < 256; i++)
            st[i] = "" + (char)i;

		boolean done = false;
		while(!done)
		{
			/** 256 is the reserved EOF for the compressed input stream **/
			int i = 257, code = BinaryStdIn.readInt(width); /** reading the first codeword **/
			String str = st[code];

			while(true) 
			{		
				/**
				 *	The codeword width in [9, 16].
				 *  Increment codeword width only when 2^width codewords have been added. 
				 */
					if(i + 1 == length) 
					{	
						/** increment code size. **/
						if(width < 16)
						{
							width++;
							length <<= 1;
						}
						/** if 16-bit bound was met, reset is performed. **/
						else
						{
							width = 9;
							length = 512;	
							BinaryStdOut.write(str);
							break;
						}
					}
					
				BinaryStdOut.write(str);
				code = BinaryStdIn.readInt(width);
				if (done = (code == 256)) break;
				String _str = st[code];
				if (i == code) _str = str + str.charAt(0);   /** special case check **/
				if (i < 65536) st[i++] = str + _str.charAt(0);
				str = _str;
			}
		}
        BinaryStdOut.close(); /** flushing out buffer **/
    }

    public static void main(String[] args) throws Exception
	{
		if(args[0].equals("-")) compress(9, 512, 257);
        else if (args[0].equals("+")) expand(9, 512);   
        else throw new RuntimeException("Illegal command line argument!");
    }
}