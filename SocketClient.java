//////////////////////////////////////////////////////////////////
//								//
//								//
//		SOCKET PROGRAMMING - CLIENT			//
//		---------------------------			//
//								//
//								//	
//////////////////////////////////////////////////////////////////

//header files
import java.io.*;
import java.util.*;
import java.net.*;

//Socket Client class
public class SocketClient
{
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	//staic temporary variables	
	public static int count1;
	public static int times;

	public void communicate()
	{
		Scanner sc = new Scanner(System.in);
	//variable to set always true condition to run the loop
		boolean condition=true; 
                  
	//temporary variables
		String status1="no";                      //variable which checks the condition to limit the 100 known users
		String status="no";			  //variable which checks if there are two users who are logged in same name
	
	//----client executable program begins from here-----	
		System.out.println("Enter your name: ");
		String name = sc.nextLine();
	        out.println(name);			  //send the username to the server
	//receive the message from the server to determine the known user limit      		
		try
      		{
         		String limit=in.readLine();
         		status1=limit;
      		} 
      		catch (IOException e)
      		{
         		System.out.println("Read failed");
         		System.exit(1);
      		}
		if(status1.equals("you can continue")) //limits 100 users.
		{
	//receive the message from the server to determine whether same user has already been logged in or not.
			try
			{
			String answer=in.readLine();
			status=answer;
			} 
      			catch (IOException e)
      			{
			System.out.println("Read failed");
			System.exit(1);
			}
			if(status.equals("continue"))//limits if two user of same name are connected.
			{
	//run the while(true) loop unless break
				while(condition==true)
				{
					System.out.println("\n1. Display the names of all known users.\n2. Display the names of all currently connected users.\n3. Send a text message to a particular user.\n4. Send a text message to all currently connected users.\n5. Send a text message to all known users.\n6. Get my messages.\n7. Exit.\n\nEnter your choice:");
					String query=sc.nextLine();   //get the menu choice
					out.println(query);	      //send menu choice to the server
					count1=Integer.parseInt(query);
	//exits the while(true) loop if the menu choice is 7									
					if(count1==7)
						break;
	//synchronize in loop with the server to recive the all known and currently connected users username
					if(count1==1||count1==2)
					{
	//receive the number of times - to establish looping server-client communication
						try
						{
							String line = in.readLine();
							times=Integer.parseInt(line);
						       
						} 
      						catch (IOException e)
      						{
         						System.out.println("Read failed");
         						System.exit(1);
      						}
   						
						for(int a=1;a<=times;a++)
						{
							try
							{
								 String answer=in.readLine();
							         System.out.println(answer);
							} 
      							catch (IOException e)
      							{
         							System.out.println("Read failed");
         							System.exit(1);
      							}
						}
					}
	//synchronize in loop with the server to get all messages of a particular user			
					if(count1==6)
					{
	//receive the number of times - to establish looping server-client communication
						try
      						{
         						String line = in.readLine();
							times=Integer.parseInt(line);
							
						} 
      						catch (IOException e)
      						{
      							System.out.println("Read failed");
      							System.exit(1);
      						}
	//if the list of messages are empty it will display the below message
   						if(times==1)
						{
							System.out.println("Nothing to display");
						}
	//prints all the messages in the list
						else
						{
							for(int a=1;a<=times;a++)
							{
								try
      								{
									String answer=in.readLine();
        								System.out.println(answer);
      								} 
      								catch (IOException e)
      								{
         								System.out.println("Read failed");
         								System.exit(1);
      								}

    							}
  						}
					}
	//synchronize in loop to create server-client communication to send message for a particular user.
					if(count1==3)
					{
						System.out.println("Enter recipient's name:");
						String dest=sc.nextLine();
      						out.println(dest);
						System.out.println("Enter a message:");
      						String msg=sc.nextLine();
      						out.println(msg);
	//receive the acknowledgment to check whether message is posted or not.
      						try
      						{
 							String reply=in.readLine();
  						        System.out.println(reply);
      						} 
      						catch (IOException e)
      						{
         						System.out.println("Read failed");
         						System.exit(1);
      						}
					}
	//synchronize in loop to create server-client communication to send message all known/currently connected users.
					if(count1==4||count1==5)
					{
						System.out.println("Enter a message to post:");
						String msg1=sc.nextLine();
						out.println(msg1);
	//receive the acknowledgment to check whether message is posted or not.
      						try
      						{
							String reply1=in.readLine();
         						System.out.println(reply1);
      						} 
      						catch (IOException e)
      						{
         						System.out.println("Read failed");
         						System.exit(1);
      						}
					}
	//for default cases- reads a message to enter a valid choice in the menu
					if(count1>7)
					{
      						try
      						{
						 	String reply2=in.readLine();
         						System.out.println(reply2);
      						} 
      						catch (IOException e)
      						{
         						System.out.println("Read failed");
         						System.exit(1);
      						}
					}

				}
			}        
		}
		else
		{
			System.out.println("server known user limit reached- cannot login - close server and restart");
		}
	}
  
	public void listenSocket(String host, int port)
	{
      //Create socket connection
		try
		{
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("Unknown host");
			System.exit(1);
		} 
		catch (IOException e) 
		{
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage:  client hostname port");
			System.exit(1);
		}
		SocketClient client = new SocketClient();
		String host = args[0];
		int port = Integer.valueOf(args[1]);
		System.out.println("Connecting to "+args[0]+": "+args[1]);
		client.listenSocket(host, port);
		client.communicate();
	}	
}

