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
		init();
	}
	
	public void init()
	{
		/**
		 *	the first level being an array instead of linked DLBnodes.
		 *	potentially memory overhead with a trivial amount, 
		 *  this design however provides codes to be built upon and ease the search at the beginning.  
		 */
			for(int i = 0 ; i < 256 ; i++)
				roots[i] = new DLBnode4LZW((char)i , i);
	}
		
	public boolean maxPrefixFound(int code, int width) throws Exception
	{
		int val; /** the first character read for each execution of maxPrefixLength(@int, @int). **/ 
		if((val = in.read()) != -1)
		{
			DLBnode4LZW root = roots[(char)val]; 
			BinaryStdOut.write(maxPrefixCode(root, root.code, code), width);
			return true;
		}
		BinaryStdOut.write(256, width); /** 256 is the reserved code for EOF of the compressed stream. **/
		BinaryStdOut.close(); /** flushing out buffer **/
		in.close(); /** to be a good citizen. **/
		return false; /** causing loop termination of the LZW process. **/ 
	}
	
	private int maxPrefixCode(DLBnode4LZW node, int codeSoFar, int code) throws Exception
	{
		int val; /** might be pushed back into the input stream if not matched. **/
		if((val = in.read()) == -1) /** EOF of the input stream reached --> returning the code found so far. **/ 
			return node.code;
			
		char ch = (char)val;
		if(node.child != null)
		{
			node = node.child; /** traversing the next level horizontally to match ch. **/
			DLBnode4LZW last = null; /** will be one DLBnode4LZW behind the current node. **/
			do
			{
				if(ch == node.ch) /** ch matched --> traversing the next level. **/
					return maxPrefixCode(node, node.code, code);
				last = node;
				node = node.sibling;
			}
			while(node != null); /** node being null <=> last being the last valid DLBnode4LZW. **/				
			last.sibling = new DLBnode4LZW(ch, code);
		}
		else /** node.child being null --> building head child for node's children-level. **/ 
			node.child = new DLBnode4LZW(ch, code);
			
		/**
		 * having not matched --> 
		 * 1.pushing back the byte read at the beginning so that it is re-readable.
		 * 2.returning the code found so far.
		 */
			in.unread(val);
			return codeSoFar;
	}
		
	private class DLBnode4LZW
	{
		private char ch;
		private int code;
		private DLBnode4LZW child;
		private DLBnode4LZW sibling;
		
		public DLBnode4LZW(char ch, int code)
		{
			this.ch = ch;
			this.code = code;
			this.child = null;
			this.sibling = null;
		}
	}
}