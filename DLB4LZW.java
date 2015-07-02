/************************
 *	@Author : You Zhou  *
 ************************/

import java.io.*;
import java.util.*;

public class DLB4LZW
{
	private DLBnode4LZW[] roots = new DLBnode4LZW[256];
	private PushbackInputStream in = null;
	private StringBuilder str = null;
	private char ch;
	private int readVal;
	private int encode;
	private int codeword;
	private int width;
	
	public DLB4LZW(PushbackInputStream in)
	{
		this.in = in;
		for(int i = 0 ; i < 256 ; i++)
			roots[i] = new DLBnode4LZW((char)i , i);
	}
	
	public boolean hasNext()
	{
		return (readVal = in.read()) != -1;
	}
	
	public StringBuilder findMaxPrefix(int codeword, int width) throws Exception
	{
		this.codeword = codeword;
		this.width = width;
		str = new StringBuilder();
		ch = (char)readVal;
		str.append(ch);
		DLBnode4LZW root = roots[ch];
		encode = root.codeword;
		if(this.hasNext())
			return findMaxPrefix(str, root);
		BinaryStdOut.write(encode, width);
		return str;
	}
	
	private StringBuilder findMaxPrefix(StringBuilder str, DLBnode4LZW node) throws Exception
	{
		ch = (char)readVal;
		if(node.child != null)
		{
			node = node.child;
			DLBnode4LZW former = null;
			do
			{
				if(ch == node.ch)
				{
					str.append(ch);
					encode = node.codeword;
					if(this.hasNext())
						return findMaxPrefix(str, node);
					BinaryStdOut.write(encode, width);
					return str;
				}
				former = node;
				node = node.sibling;
			}
			while(node != null);				
			former.sibling = new DLBnode4LZW(ch, codeword);
		}
		else node.child = new DLBnode4LZW(ch, codeword);

		in.unread(readVal);
		BinaryStdOut.write(encode, width);
		return str;
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