
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The two_pass_linker class is responsible for reading from scanner input, 
 * creating a symbol table, and a memory map.
 * The program is interactive. 
 * When the program is executed the input will be provided as the program's 
 * command line arguments. This class processes the input twice: once for 
 * determining the base address for each module and the absolute address for 
 * each external signal, storing the later in the symbol table it produces.
 * The second pass uses the base addresses and the symbol table computed in 
 * pass one to generate the actual output by relocating relative addresses 
 * and resolving external references.
 *
 * 
 * 
 * @author Christina Liu 
 *
 */

public class Linker{

	public static void main(String[] args){
		
		System.out.println("Enter input: ");
		//creating the two passes
		Scanner pass_one = new Scanner(System.in);
		Scanner pass_two = new Scanner(System.in);

		//pass through linker method
		link(pass_one, pass_two);

		//closing the two passes
		pass_one.close(); 	
		pass_two.close();
	}

	/**
	 * Returns nothing it is a void function
	 * @param the two scanners that correspond to the two passes
	 * @returns nothing
	 */

	public static void link(Scanner pass_one, Scanner pass_two) {

		//creating the symbol table and the address table 
		ArrayList<String> symbolTable = new ArrayList<String>();
		ArrayList<String> addresses = new ArrayList<String>();
		ArrayList<Integer> sizes = new ArrayList<Integer>();

		//initializing variables 
		int num = 0;
		int baseAddress = 0;
		int moduleSize = 0;
		int relativeAddress = 0;
		int address = 0;

		//getting the number of modules in the input
		int mods = pass_one.nextInt();

		//checking if there is more input 
		while(pass_one.hasNext()) {

			//starting the symbol table output 
			System.out.println("");
			System.out.println("Symbol Table");		
			for (int i = 0; i < mods; i++){
				num = pass_one.nextInt();

				//get the symbol
				String symbol = null;
				for (int j = 0; j < num; j++){
					symbol = pass_one.next();

					//checking for if the symbol is multiply defined else get the address of it 
					if (symbolTable.indexOf(symbol) != -1){
						System.out.println("Error: " + symbol + " is multiply defined.");
					}
					else 
					{
						relativeAddress = pass_one.nextInt();
						address = baseAddress + relativeAddress;
						symbolTable.add(symbol);
						addresses.add(Integer.toString(address));

						//prints the symbol table-- for the first pass (just what the symbol is equal to)
						System.out.println(symbol + " = " + address);
					}
				}

				//count which address corresponds to the symbol using the defined number
				int uses = pass_one.nextInt();
				for (int k = 0; k < uses; k++){
					pass_one.next();
				}

				//add this to the moduleSize array 
				moduleSize=pass_one.nextInt();
				sizes.add(moduleSize);

				//increment the base address 
				baseAddress += moduleSize;
				for (int a = 0; a < moduleSize; a++){
					pass_one.nextInt();
				}
			}			
		}

		//end of first pass, start of second pass 
		while (pass_two.hasNext()) {

			//starting the memory map output
			System.out.println("Memory Map");

			//initializing variables + new variables 
			baseAddress=0; moduleSize =0; address =0;
			int numDec=0;
			int last=0;
			int initialaddress=0;
			int finaladdress=0;
			String symbol = null;
			int symbolIndex=0;
			ArrayList<String> symbols = new ArrayList<String>();
			ArrayList<String> use = new ArrayList<String>();

			//passing through number of modules 
			for (int i =0; i < mods; i++) {
				num = pass_two.nextInt();
				for (int j =0; j < num; j++) {
					pass_two.next();
					pass_two.next();
				}

				//checking the number of uses to store in uselist 
				int uses = pass_two.nextInt();
				String useList[] = new String[uses];
				for (int k =0; k < uses; k++) {
					useList[k] = pass_two.next();
					symbols.add(useList[k]);
				}

				//getting module size
				moduleSize = pass_two.nextInt();
				

				//finding out how the address changes based on last digit 
				//also a lot of error handling in this section 
				for (int a =0; a < moduleSize; a++) {
					address =-1;
					numDec = pass_two.nextInt();
					last = numDec % 10;
					initialaddress = numDec /10;
					//last digit is 1
					//immediate address -> unchanged
					if (last == 1) {
						finaladdress = initialaddress;
					}
					//last digit is 2
					//absolute address -> unchanged
					else if (last == 2) {
						finaladdress = initialaddress;
						if (finaladdress - (int)(Math.floor(finaladdress/1000)*1000)>500) {
							System.out.println("Error: Abolute address exceeds machine size.");
							finaladdress = (int)(Math.floor(finaladdress/1000)*1000);
						}
					}
					//last digit is 3
					//relative address -> relocate
					else if (last == 3) {
						if (initialaddress - (int)(Math.floor(initialaddress/1000)*1000) > moduleSize) {
							System.out.println("Error: Relative address exceeds module size");
							finaladdress = initialaddress;
						}
						else {
							finaladdress = initialaddress + baseAddress;
						}
					}
					//last digit is 4
					//external -> resolve
					else if (last == 4) {
						symbolIndex = initialaddress - (int)(initialaddress/1000) *1000;
						if (symbolIndex > useList.length -1) {
							System.out.println("Error: External address exceeds length of use list.");
							finaladdress = initialaddress;
						}
						symbol = useList[symbolIndex];
						use.add(symbol);
						if (symbolTable.indexOf(symbol) == -1){
							System.out.println("Error: " + symbol + "used but not defined");
							address = 0;
						}
						else{
							address = Integer.parseInt(addresses.get(symbolTable.indexOf(symbol)));
						}
						finaladdress = initialaddress + address - symbolIndex;
					}

					//printing out memory map output
					System.out.println((a + baseAddress) + " : " + finaladdress);
				}
				baseAddress +=moduleSize;
			}

			//last bits of error handling 
			for (int b = 0; b < symbolTable.size(); b++) {
				if (symbols.indexOf(symbolTable.get(b)) == -1) {
					System.out.println(symbolTable.get(b) + " is defined, not used.");
				}
			}
			for (int c = 0; c < symbols.size(); c++) {
				if (use.indexOf(symbols.get(c))==-1) {
					System.out.println(symbols.get(c) + " appears in use list but is never used.");
				}
			}
		
		}	
	}
}
