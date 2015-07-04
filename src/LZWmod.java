/*************************************************************************
 *	@author: You Zhou
 *  E-mail : yoz13@pitt.edu
 *************************************************************************/

import java.io.*;
import java.util.*; 
 
public class LZWmod
{	
	/** making variable scope as limiting as possible. **/
	public static void compress(int width, int length, int code) throws Exception 
	{ 
		DLB4LZW dlb = new DLB4LZW(new PushbackInputStream(System.in));
		/** FALSE as having reached EOF of the input stream. **/
		while(dlb.maxPrefixFound(code++, width, code < 65536))
			/** having exhausted all of the current legitimate codewords. **/
			if(code == length && width < 16) 
			{	
				width++;
				length <<= 1;
			}
	}
 

	/** making variable scope as limiting as possible. **/
    public static void expand(int width, int length) 
	{			
        String[] st = new String[65536]; /** safeSize = 2^16. **/
		int i; /** initialize symbol table with all 1-character strings. **/
		for(i = 0; i < 256; i++)
            st[i] = "" + (char)i;
        st[i++] = ""; /** 256 is the reserved EOF for the compressed input stream **/

		/** reading the first codeword **/
        int code = BinaryStdIn.readInt(width);
        String str = st[code];

		while(true) 
		{				
		   /** incrementing the codeword width only when 2^width codewords have been added. **/
			if(i + 1 == length && width < 16)
			{
				width++;
				length <<= 1;
			}
			
            BinaryStdOut.write(str);
            code = BinaryStdIn.readInt(width);
            if (code == 256) break;
            String _str = st[code];
            if (i == code) _str = str + str.charAt(0);   /** special case check **/
			st[i++] = str + _str.charAt(0);
			
			/** having exhausted all legitimate codewords. **/
			if(i == 65536)
			{
				BinaryStdOut.write(_str);
				break; 
			}	
			str = _str;
        }
		
		while((code = BinaryStdIn.readInt(16)) != 256)
			BinaryStdOut.write(st[code]);
        
		BinaryStdOut.close(); /** flushing out buffer **/
    }

    public static void main(String[] args) throws Exception
	{
		if(args[0].equals("-")) compress(9, 512, 257);
        else if (args[0].equals("+")) expand(9, 512);   
        else throw new RuntimeException("Illegal command line argument!");
    }
}