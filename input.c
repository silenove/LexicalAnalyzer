int  cmp(int a,int b) 
{
	int count=a + b;
	while(count>b){
		if(a>b){
			count = count - a;
		}
		else{
			count = count - b;
		}
	}
	return count;
}