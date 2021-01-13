package com.vandong.tlv;

import java.util.Scanner;



public class TestEMVTLVtags {

	public static final Tag TAG_DF0D_ID = new Tag(0xdf, 0x0d);
    public static final Tag TAG_DF7F_VERSION = new Tag(0xdf, 0x7f);
    private static final VDEMVParser LOG = new VDEMVParser();
    public static final Tag T_EF = new Tag(0xEF);
	public static void main(String[] args) throws Exception { 
		// TODO Auto-generated method stub\
		
//		System.out.println(""+ );
		Scanner sc = new Scanner(System.in); 
		
		while (true) {	
		System.out.print("Input DE 55:");
		String hex = sc.nextLine();
	//	String hex = "9F02060000000001009F03060000000000009F1A020704950580000480005F2A0207049A031612319C01009F37045F17A56F820218009F360200019F10200FA501A000F8000000000000000000000F0000000000000000000000000000009F2608FAC3B60382F042D99F2701809F34030203008407A0000007271010";
    	byte[] bytes = HexUtil.parseHex(hex);   	
//    	System.out.println("hex " + bytes );
    	System.out.println("========================================Result:========================= " );
    	 EmvTlvParser parser = new EmvTlvParser();
         Tlvs tlvs = parser.parse(bytes, 0, bytes.length);
 //       System.out.println("tlvs" + tlvs );
        
        EmvTlvLogger.log("Tag:", tlvs, LOG);
   
        System.out.println("=========================================================================" );
	
}
	}
}

