# Mobile Agent Execution Platform

This project implements a mobile-agent execution platform that is in general facilitated with three distributed-computing technologies: RPC, dynamic linking, and object serialization/deserialization. This program exercise how to use these technologies in Java, which correspond to RMI, class loader and reflection, and Java object input/output streams. 

## Introduction:

A user must derive an application-specific mobile agent from the Mobile.Agent base class that is instantiated as an independent thread at run time. The mobile agent must implement at least two methods: 

i) the constructor receiving a String array and 

ii) the init( ) method that is invoked right after the constructor execution. The mobile agent is executed in the following four steps. 

## Step 1: Injection 

The agent is instantiated where a user injects it through the Mobile.Inject program, (i.e. the computing node local to the user), and receives a String array as the constructor argument. At this time, an agent is still an ordinary (passive) Java object. 

## Step 2: System-initiated migration 

Upon an instantiation, the agent is dispatched to the computing node that has been specified with the Mobile.Inject program. Dispatched there, the agent starts to run as an independent thread and automatically invokes its init( ) method. If init( ) has no hop( ) method call, a return from init( ) means the termination of this agent.  

## Step 3: User-initiated migration 

If the agent invokes the hop( destination, function, arguments ) function within init( ), it will migrate to the next computing-node, (i.e., destination) specified in hop( ). Upon each user-initiated migration, the 
agent will resume its execution as an independent thread and invoke the function specified in hop( ). 

## Step 4: Termination 

If the agent returns from the function that was invoked upon a migration (including init( )), the thread to run this agent is stopped and the object is garbage-collected by the system.

## Methods:

1. public void hop( String host, String function ) Transfers this agent to a given host and invokes a given function of this agent.
2. public void hop( String host, String function,String[] arguments ) Transfers this agent to a given host, and invokes a given function of this agent as passing given arguments to it.
3. public void run( ) Is the body of Mobile.Agent that is executed upon an injection or a migration as an independent thread. The run( ) method identifies the function and arguments given in hop( ), and invokes it. The invoked function may include hop( ) to further transfer the calling agent to a remote host or simply return to run( ) that terminates the agent.
4. public void setPort( int port ) Sets a port that is used to contact a remote RMI server when migrating there.
5. public void setId( int id ) Sets this agent identifier, (i.e., id).
6. public int getId( ) Returns this agent identifier, (i.e., id).
7. public static byte[] getByteCode( String className ) Reads a byte code from the file whose name is className + “.class”.
8. public byte[] getByteCode( ) Reads this agent's byte code from the corresponding file.
9. private byte[] serialize( ) Serializes this agent into a byte array.

**Agent.run( ) performs the following tasks:** 
(1) Find the method to invoke, through this.getClass( ).getMethod( ). 
(2) Invoke this method through Method.invoke( ). 
 
**Agent.hop( String hostname, String function, String[] args ) performs the following tasks:** 
(1) Load this agent’s byte code into the memory. 
(2) Serialize this agent into a byte array. 
(3) Find a remote place through Naming.lookup( ). 
(4) Invoke an RMI call. 
(5) Kill this agent with Thread.currentThread( ).stop( ), which is deprecated but do so anyway.

**Place.main( String args[] ) performs the following tasks:** 
(1) Read args[0] as the port number and checks its validity. 
(2) Invoke startRegistry( int port ). 
(3) Instantiate a Place object. 
(4) Register it into rmiregistry through Naming,rebind( ). 
 
**Place.transfer( String classname, byte[] bytecode, byte[] entity ) performs the following tasks:** 
(1) Register this calling agent’s classname and bytecode into AgentLoader. 
(2) Deserialize this agent’s entity through deserialize( entity ). 
(3) Set this agent’s identifier if it has not yet been set. New agent id is given by having each Place maintain a sequencer, to generate a unique 
agent id with a combination of the Place IP address and this sequence number, and increment the sequencer. 
(4) Instantiate a Thread object as passing the deserialized agent to the constructor. 
(5) Invoke this thread’s start( ) method. 
(6) Return true if everything is done in success, otherwise false.  

## Documentation: 

**Agent.java:** The Agent class file is the base class for all mobile agents in the system.

**run() method:** Supports method invocation both with and without arguments. First, it tries to find a remote system using RMI lookup. Then, it checks for any waiting messages and receives them. After that, it uses Java Reflection to call the given method. If the method has no arguments, it uses getMethod() without parameters. If the method needs arguments, it passes a String array and runs the method using those arguments, which are cast to an Object.

**hop() method:** Enables agent migration by first collecting the agent's bytecode, then setting target destination, function to invoke, and arguments. It serializes the agent, uses RMI to transfer itself to the destination host by calling the remote place.transfer() method, and terminates its current thread execution using Thread.currentThread().stop().

**Communication Feature:** This allows agents to send messages to other hosts. These messages are stored at the destination until they are retrieved.
The Place class implements the environment where agents run.

**main() method:** Checks if the port number is valid. Then, it starts the local RMI registry by calling startRegistry(), creates a new Place object, and registers it using Naming.rebind() to make it available for incoming agent requests and connect to it.

**transfer() method:** Accepts the incoming agents. It loads the class of the agent using loader.loadClass(), deserializes the agent using deserialize(), and gives it a unique ID by combining the host's hash code and a counter. Then, it starts a new thread for the agent and runs it.
Message handling: send() and receive() methods are used to store and get messages. A HashMap is used to connect agent IDs to message lists. This allows agents to communicate with each other indirectly.

The inter-agent communication system allows agents to send messages to other hosts using sendMessage(). The messages are stored under the agent’s ID in the receiving Place. When the agent arrives at the new host, it can check for and read the messages. These messages are also shown automatically when the agent reaches its destination.


