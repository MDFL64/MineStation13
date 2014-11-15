package rocks.cogg.ms13;

enum AirlockType {
	ASSEMBLY ("assembly",false,false),
	EXTERNAL ("external",false,false),
	INTERNAL ("internal",false,false);
	
	public final String name;
	public final boolean sideways;
	public final boolean superSecure;
	
	AirlockType(String n,boolean sw,boolean sc) {
		name=n;
		sideways=sw;
		superSecure=sc;
	}
}