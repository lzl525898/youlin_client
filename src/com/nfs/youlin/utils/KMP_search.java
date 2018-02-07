package com.nfs.youlin.utils;

import android.util.Log;

public class KMP_search {
	private static void get_skippattern(String pattern, int[] next, int len)
	{
	    int pos = 2;
	    int subStrIndex = 0; //valid prefix candidate substring index;
	    next[0] = -1; // when 1st char mismatched, always move 1 (p=0, k=-1);
	    next[1] = 0;  // when 2nd mismatched, always move 1(p=1, k=0);in fact, if the 2nd char is same as 1st char, we can move 2
	    while(pos<len)
	    {
	        if(pattern.substring(pos-1, pos) == pattern.substring(subStrIndex, subStrIndex+1)) //one char matched, then continue to match more, 
	        {
	            subStrIndex++;            //prefix substring move ahead;
	            next[pos] = subStrIndex;//for current position, the k is got; 
	            pos++;                    //current pos move ahead;
	        }
	        else if(subStrIndex>0)    //one substring found, but in the new pos, mismatched;
	        {
	            subStrIndex = next[subStrIndex]; //then we need fall back subStrIndex to value that still can be matched;
	        }
	        else
	        {
	            next[pos] = 0;
	            pos++;
	        }
	    }
	}
	public static int KMP_search(String src, int slen, String pattern, int plen)
	{
	  //  int* next = (int *)malloc(sizeof(int)*slen);
	    int[] next = new int[slen];
	    if(slen>1){
	    	get_skippattern(pattern,next,plen);
	    }else{
	    	next[0]=-1;
	    }
	    

	    int indexInSrc = 0;
	    int offset = 0;
	    while((indexInSrc+offset)<slen)
	    {
//	    	Loger.d("hyytest","pattern="+ pattern.substring(offset, offset+1));
//        	Loger.d("hyytest","src="+ src.substring(indexInSrc+offset, indexInSrc+offset+1));
	        if(pattern.substring(offset, offset+1).trim().equals(src.substring(indexInSrc+offset, indexInSrc+offset+1).trim()))
	        {
//	        	Loger.d("hyytest", "match"+offset);
	            if(offset == (plen-1))
	                return indexInSrc;
	            offset++;
	        }else
	        {
	            indexInSrc += offset-next[offset];
	            if(next[offset]>-1)
	                offset = next[offset];
	            else
	                offset = 0;
	        }
	    }
	    return slen;
	}
}
