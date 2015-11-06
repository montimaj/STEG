package stegano.utilities;

public class GenKey 
{
	private int ncols, nencrypt;
	/**
	 *  
	 * @param s Input secret key string
	 */
	public GenKey(String s)
	{
		gen_key(s);
	}
	/**
	 * 
	 * @return number of columns
	 */
	public int get_colsize()
	{
		return ncols;
	}
	/**
	 * 
	 * @return number of times the encryption algorithm will have to done
	 */
	public int get_encryption_number()
	{
		return nencrypt;
	}
	private int calc_nums(String s)
	{
		int sum=0;
		for(int i=0;i<s.length();++i)
			sum+=(s.charAt(i)-48)*(i+1);
		return sum;
	}
	private String reverse_string(String s)
	{
		String s1="";
		for(int i=s.length()-1;i>=0;--i)
			s1+=s.charAt(i);
		return s1;
	}
	private void gen_key(String skey)
	{
		int j=17,mat[]=new int[17];
		for(int i=1;i<17;++i)
			mat[i]=j--;
		int base=mat[skey.length()];
		long sum=0;
		for(int i=0;i<skey.length();++i)
			sum+=(int)skey.charAt(i)*Math.pow(base, i+1);
		String s=""+sum;		
		int val=calc_nums(s);
		ncols=(int)(sum%val);		
		if(ncols==0)
			ncols=val;
		else if(ncols>256)
			ncols=256;
		s=reverse_string(s);
		val=calc_nums(s);
		nencrypt=(int)(sum%val);		
		if(nencrypt==0)
			nencrypt=val;	
		else if(nencrypt>64)
			nencrypt=64;
	}	
}
