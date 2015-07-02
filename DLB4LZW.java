/************************
 *	@Author : You Zhou  *
 ************************/

import java.io.*;
import java.util.*;

public class DLB4LZW
{
	private DLBnode4LZW[] roots = new DLBnode4LZW[256];
	private PushbackInputStream in = null;
	
	public DLB4LZW(PushbackInputStream in)
	{
		this.in = in;
		
		/**
		 *	the first level being an array instead of linked DLBnodes.
		 *	potentially memory overhead with a trivial amount, 
		 *  this design however provides codewords to be built upon and ease the search at the beginning.  
		 */
			for(int i = 0 ; i < 256 ; i++)
				roots[i] = new DLBnode4LZW((char)i , i);
	}
	
	public int maxPrefixLength(int codeword, int width) throws Exception
	{
		int input = in.read(); /**
		                        * the first character read for each maxPrefixLength(@int, @int).
								* the corresponding root is guaranteed to be found in O(1) using roots[index], given being non-negative.
								* returning -1 to signify EOF of the input stream.
								*/
		if(input != -1)
		{
			char ch = (char)input; 
			StringBuilder str = new StringBuilder();
			
			/**
			 *	while writing out the codeword found is the main purpose, 
			 *	@parameter : str is updated along with the execution of maxPrefixCode(@StringBuilder, @DLBnode4LZW, @int). 
			 */
				BinaryStdOut.write(maxPrefixCode(str.append(ch), roots[ch], codeword), width);
			
			return str.length();
		}
		BinaryStdOut.write(256, width); /** 256 is the reserved codeword for EOF of the compressed stream. **/
		return input; /** returning -1 causes loop termination of the LZW process. **/ 
	}
	
	private int maxPrefixCode(StringBuilder str, DLBnode4LZW node, int codeword) throws Exception
	{
		int val; /** might be pushed back into the input stream if not matched. **/
		if((val = in.read()) == -1) /** EOF of the input stream reached --> returning the codeword found so far. **/ 
			return node.codeword;
			
		char ch = (char)val;
		if(node.child != null)
		{
			node = node.child; /** traversing the next level horizontally to match ch **/
			DLBnode4LZW last = null; /** will be one DLBnode4LZW behind the current node **/
			do
			{
				if(ch == node.ch) /** ch matched --> traversing the next level **/
					return maxPrefixCode(str.append(ch), node, codeword);
				last = node;
				node = node.sibling;
			}
			while(node != null); /** node being null <=> last being the last valid DLBnode4LZW **/				
			last.sibling = new DLBnode4LZW(ch, codeword);
		}
		else /** node.child being null --> building head node for node's children-level **/ 
			node.child = new DLBnode4LZW(ch, codeword);
			
		/**
		 * having not matched --> 
		 * 1.pushing back the byte read at the beginning so that it is re-readable.
		 * 2.returning the codeword found so far.
		 */
			in.unread(val);
			return node.codeword;
	}
	
		private class DLBnode4LZW
		{
			private char ch;
			private int codeword;
			private DLBnode4LZW child;
			private DLBnode4LZW sibling;
			
			public DLBnode4LZW(char ch, int codeword)
			{
				this.ch = ch;
				this.codeword = codeword;
				this.child = null;
				this.sibling = null;
			}
		}
}