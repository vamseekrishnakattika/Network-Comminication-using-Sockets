//////////////////////////////////////////////////////////////////
//								//
//								//
//		SOCKET PROGRAMMING - SERVER			//
//		---------------------------			//
//								//
//								//	
//////////////////////////////////////////////////////////////////

//header files
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import java.lang.*;
import java.net.*;
import java.util.concurrent.Semaphore;


//this class database stores the message database in the server
//It holds the sender username, receiver username, message content, date and time when the message is posted.
class database
{
	public String from;
	public String to;
	public String msg;
	//declare the date format
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	public String date; 
	public database(String fromAddress,String toAddress,String message)
	{
		from=fromAddress;
		to=toAddress;
		msg=message;
		date= dateFormat.format(timestamp);
		//stores the first 80 characters of the message- trims the remaining
		msg = msg.substring(0, Math.min(msg.length(), 80));
	}
	public String getSender()
	{
		return from;
	}
	public String getReceiver()
	{
		return to;
	}
	public String getMessage()
	{
		return msg;
	}
	public String getDate()
	{
		return date;
	}	
}

//ClientWorker Class
//It creates a thread for each client in the server.
class ClientWorker implements Runnable 
{
	private Socket client;
	ClientWorker(Socket client) 
	{
		this.client = client;
	}
	//Declaring semaphores to provide mutual exclusion when threads tries to access the same list.
	static Semaphore mutex1 = new Semaphore( 1);//for knownUser list
	static Semaphore mutex2 = new Semaphore( 1);//for currentUser list
	static Semaphore mutex3 = new Semaphore( 1);//for messageDatabase list 

	//Declaring lists to store known user , current user and message database- list of objects-database.
	public static List<database> messageDatabase = new ArrayList<database>();
	public static List<String> currentUser = new ArrayList<String>();
	public static List<String> knownUser = new ArrayList<String>();

	//temporary variable to hold the values	
	public static int count=0;
	String name;
	String command1,command2,command3,command4;
	public static String[] currentlyConnected=new String[100];
	public String command; 

	//Declaring the date format 
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

	//to safely remove the list items
	Iterator<database> iter = messageDatabase.iterator();

	public void run()
	{ 
	//temporary variables
		boolean condition=true;      
      		String query;
      		String line;
		boolean search=false;
		boolean currentUserSearch=false;
		boolean limit = true;
	
	//socket read and write
		BufferedReader in = null;
      		PrintWriter out = null;
		try 
      		{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
      		} 
      		catch (IOException e) 
      		{
	 		System.out.println("in or out failed");
	 		System.exit(-1);
      		}
	//---------server executable code starts from here---------
	//receive the username from client
		try 
      		{
			line = in.readLine();
			name=line;	 
		} 
      		catch (IOException e) 
      		{
	 		System.out.println("Read failed");
	 		System.exit(-1);
      		}
	//search in the knownUser list to find match of the username
		for(String str: knownUser)
		{
    			if(str.trim().contains(name))
       			{
 				search=true;
         		}
		}
		int knownUserLength=knownUser.size();
	//search in the currentUser list to find match of the username
		for(String str: currentUser)
		{
    			if(str.trim().contains(name))
       			{
 				currentUserSearch=true;
         		}
		}
	//gives the limiting condition for 100 known users
	//beyond the 100 known users the server will saturate and will not allow new user login 
		if(!search&&knownUserLength>3)
		{
			limit=false;
			out.println("known users limit reached");
		}
		else
		{
			out.println("you can continue");
		}
		if(limit)
		{	
	//sets the condition to check whether the username is already logged in or not.
			if(currentUserSearch)
			{
				out.println("Already logged in");
				String text = dateFormat.format(timestamp);
				System.out.println(text+", "+name+" tried to connect- Already logged in- disconnected");
			}
			else
			{
				out.println("continue");
			}
	//determines whether the user is known or unknown user
			if(!currentUserSearch)
			{	
				currentUser.add(name);
				if(search)
				{
					currentlyConnected[count++]=name;
					String text = dateFormat.format(timestamp);
					System.out.println(text+", Connection by known user "+currentlyConnected[count-1]);
				}
				else
				{    
					
					knownUser.add(name);
					currentlyConnected[count++]=name;
					String text = dateFormat.format(timestamp);
					System.out.println(text+", Connection by unknown user "+currentlyConnected[count-1]);
				}
	//while(true)loop loops forever until break at case 7
				while(condition==true)
				{
	//reads the menu choice of the client
					try 
					{
						query=in.readLine();
						command=query;
      					} 
      					catch (IOException e) 	
      					{
	 					System.out.println("Read failed");
	 					System.exit(-1);
      					}
     	
	//switches the control according to the menu choice by the client				
					switch(command)
      					{
	//the menu choice case 1 is used to display all known users
      						case "1":
						{
	
							String text1 = dateFormat.format(timestamp);
							System.out.println(text1+", "+currentlyConnected[count-1]+" displays all known Users. ");
					//sorts the username in alphabetical order							
							Collections.sort(knownUser);
							int a=0,b=0;
							a=knownUser.size();
							b=a+1;
					//sends the number of times for the client to receive
							out.println(b);
					
							if(b!=1)
							{
								out.println("Known Users:");
								for(int i=0;i<knownUser.size();i++)
								{
									int j=i+1;
									query=j+"."+knownUser.get(i);
        								out.println(query);
								}
							}
							break;
						}
	//menu choice case 2 is provided to display all currently connected users
      						case "2":
						{
        						String text1 = dateFormat.format(timestamp);
 							System.out.println(text1+", "+currentlyConnected[count-1]+" displays all Connected Users. ");
					//sorts the currentUser list alphabetically
							Collections.sort(currentUser);
							int a=0,b=0;
							a=currentUser.size();
						        b=a+1;
					//sends the number of times the communication takes place
							out.println(b);
							if(b!=1)
							{
								out.println("Current Users:");
								for(int i=0;i<currentUser.size();i++)
								{
									int j=i+1;
									query=j+"."+currentUser.get(i);
        								out.println(query);
								}
							}
							break;
						}
	//the menu choice 3 is used to send message to a particular user 
						case "3":
						{
				//receive the receipient's name 
     							try 
      							{
	 							query=in.readLine();
								command1=query;
      							} 
      							catch (IOException e) 	
      							{
	 							System.out.println("Read failed");
	 							System.exit(-1);
      							}
				//receive the message content
							try 
      							{
								query=in.readLine();
								command2=query;
      							}	 
      							catch (IOException e) 	
      							{
	 							System.out.println("Read failed");
	 							System.exit(-1);
      							}
							String text1 = dateFormat.format(timestamp);
 							System.out.println(text1+", "+currentlyConnected[count-1]+" posts message for "+command1);

							messageDatabase.add(new database(name,command1,command2));
							
				//acknowledgment to client that the message has been posted
							out.println("Message posted to "+command1);
							boolean search1=false;
							for(String str: knownUser)
							{
    								if(str.trim().contains(command1))
       								{
 									search1=true;
         							}
							}
				//if the message is sent to an unknown user- he becomes known
							if(!search1)
							{
								knownUser.add(command1);
								
							}
							break;
						}
	//the menu choice 4 gives is used to send the message all currently connected users
						case "4":
						{
				//read the message from the sender
     							try 
							{
								query=in.readLine();
								command3=query;
							} 
							catch (IOException e) 	
							{
 								System.out.println("Read failed");
 								System.exit(-1);
							}
							String text1 = dateFormat.format(timestamp);
							System.out.println(text1+", "+currentlyConnected[count-1]+" posts message for all currently connected users.");
					        	
							for(int i=0;i<currentUser.size();i++)
							{
								
								messageDatabase.add(new database(name,currentUser.get(i),command3));
							}
							

							out.println("Message posted to all currently connected users");
							break;
						}
	//the menu choice 4 gives is used to send the message all known users
						case "5":
						{
	     						try 
      							{
								query=in.readLine();
								command4=query;
      							} 
      							catch (IOException e) 	
      							{
	 							System.out.println("Read failed");
	 							System.exit(-1);
      							}

							String text1 = dateFormat.format(timestamp);
 							System.out.println(text1+", "+currentlyConnected[count-1]+" posts message for all known users.");
				        	
							
							for(int i=0;i<knownUser.size();i++)
							{
								messageDatabase.add(new database(name,knownUser.get(i),command4));
							}
							
				//acknowledgment to the client stating that the message has been posted
							out.println("Message posted to all known users");
							break;
						}
	//menu choice 6 is used to get the message of the client
						case "6":
						{
							int count1=0;
							int count2=0;
				//search the total number of message for the client in the messageDatabase list
							for(int i=0;i<messageDatabase.size();i++)
							{
								if(messageDatabase.get(i).getReceiver().equals(currentlyConnected[count-1]))
								{
									if(!messageDatabase.get(i).getSender().equals(currentlyConnected[count-1]))
									{
										count1++;
									}	
								}
							}
				//displays only recent 10 messages of the client
				//read messages are removed 
				//the client can view the previous messages by pressing 6 again
				//messages are displayed untill it mail box empties - similar to simple gmail sytem
							int b=(count1>10)?11:count1+1;
							
        						out.println(b);
							
							if(b!=1)
							{
								out.println("Your messages:");
								while(iter.hasNext()&&count2+1!=b)
								{
									database msgDatabase = iter.next();
									if(msgDatabase.getReceiver().equals(currentlyConnected[count-1]))
									{
					
										if(!msgDatabase.getSender().equals(currentlyConnected[count-1]))
										{
											out.println("From "+msgDatabase.getSender()+", "+msgDatabase.getDate()+", "+msgDatabase.getMessage());
											iter.remove();	
											count2++;
										}
											
									}
															
								}

							}
							String text1 = dateFormat.format(timestamp);
							System.out.println(text1+", "+currentlyConnected[count-1]+" gets messages.");	
							break;
						}
	//menu choice 7 is used to terminate the thread created for each client running
      						case "7":
						{
			//currentUser name is removed from the list				
							for(int i=0;i<currentUser.size();i++)
							{
								if(currentUser.get(i)==name)
								{
									currentUser.remove(i);
								}
							}
							String text = dateFormat.format(timestamp);
							System.out.println(text+" "+currentlyConnected[count-1]+" exits");
							condition=false;
							break;
						}
			//default case handles the invalid menu options provided by the user.
						default:
						{
							out.println("Invalid menu choice");
							break;
						}

					}
 				}    
			}
		}
		else	
		{
			System.out.println("Known User limit reached - Cannot connect to anyusers");
		}	
		try 
      		{
	 		client.close();
      		}	 
      		catch (IOException e) 
      		{
	 		System.out.println("Close failed");
	 		System.exit(-1);
      		}

   	}
}

class SocketThrdServer 
{
	ServerSocket server = null;
	public void listenSocket(int port)
	{
		try
		{
			server = new ServerSocket(port); 
			
		} 
      		catch (IOException e) 
      		{
	 		System.out.println("Error creating socket");
	 		System.exit(-1);
      		}
      		while(true)
      		{	
		//creates a thread for the client running 
         		ClientWorker w;
         		try
         		{
            			w = new ClientWorker(server.accept());
            			Thread t = new Thread(w);
            			t.start();
	 		} 
	 		catch (IOException e) 
	 		{
	 			System.out.println("Accept failed");
	    			System.exit(-1);
         		}
      		}
   	}
	protected void finalize()
	{
		try
		{
			server.close();
		} 
		catch (IOException e) 
		{
         		System.out.println("Could not close socket");
         		System.exit(-1);
      		}
   	}
	public static void main(String[] args)
   	{
      		if (args.length != 1)
      		{
         		System.out.println("Usage: java SocketThrdServer port");
	 		System.exit(1);
      		}
		String hostname = "Unknown";
	//to dispaly the connected host name and port
		try
		{
		    InetAddress addr;
		    addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		    System.out.println("Server is running on "+hostname+": "+args[0]);
		}
		catch (UnknownHostException ex)
		{
		    System.out.println("Hostname cannot be resolved");
		}
		SocketThrdServer server = new SocketThrdServer();
      		int port = Integer.valueOf(args[0]);
      		server.listenSocket(port);
   	}
}

