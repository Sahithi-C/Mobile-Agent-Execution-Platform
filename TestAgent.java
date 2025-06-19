import Mobile.*;

/**
 * TestAgent is a test mobile agent that is injected to the 1st Mobile.Place
 * platform to print the breath message, migrates to the 2nd platform to
 * say "Hello!",  moves to the 3rd platform to say "Oi!", and finally to 4th platform to say Goodbye!.
 * 
 * @author  Sahithi C
 * @version 
 * @since   
 */
public class TestAgent extends Agent {
    public int hopCount = 0;
    public String[] destination = null;
    
    /**
     * The consturctor receives a String array as an argument from 
     * Mobile.Inject.
     *
     * @param args arguments passed from Mobile.Inject to this constructor
     */
    public TestAgent( String[] args ) {
	destination = args;
    }

    /**
     * init( ) is the default method called upon an agent inject.
     */
    public void init( ) {
	System.out.println( "TestAgent( " + agentId + ") invoked init: " +
			    "hop count = " + hopCount +
			    ", next dest = " + destination[hopCount] );
	String[] args = new String[1];
	args[0] = "TestAgent: Hello!";
	hopCount++;
	hop(destination[0], "step", args);
    }
    
    /**
     * step( ) is invoked upon an agent migration to destination[0] after 
     * init( ) calls hop( ).
     * 
     * @param args arguments passed from init( ).
     */
    public void step( String[] args ) {
	System.out.println( "TestAgent(" + agentId + ") invoked step: " +
			    "hop count = " + hopCount +
			    ", next dest = " + destination[hopCount] + 
			    ", message = " + args[0] );
	args[0] = "TestAgent: Oi!";
	hopCount++;
	hop(destination[1], "jump", args);

    }

    /**
     * jump( ) is invoked upon an agent migration to destination[1] after
     * step( ) calls hop( ).
     *
     * @param args arguments passed from step( ).
     */
    public void jump( String[] args ) {
	System.out.println( "TestAgent( " + agentId + ") invoked jump: " +
			    "hop count = " + hopCount +
			    ", message = " + args[0] );

	// If there is a third destination
	if (destination.length > 2 && hopCount < destination.length) {
            args[0] = "TestAgent: Goodbye!";
            hopCount++;
            hop(destination[2], "finalStop", args);
        } else {
            // If there's no third destination, send message and return
            sendMessage("TestAgent: Completed hops from " + destination[0] + " and " + destination[1],
                    getSpawnedHostName());
            System.out.println("TestAgent: Message sent to first spawned host.");
            
            // Return to the original host to check for messages
            hop(getSpawnedHostName(), "checkMessages");
        }
    }

    /**
     * finalStop() is invoked when the agent migrates to destination[2] 
     *
     * @param args arguments passed from jump( ).
     */
    public void finalStop( String[] args ) {
        System.out.println( "TestAgent( " + agentId + ") invoked finalStop: " +
                           "hop count = " + hopCount +
                           ", message = " + args[0] );
        
        // Send message about all hops
        String hopMessage = "TestAgent: Completed hops from " + destination[0] + 
                           ", " + destination[1];
        if (destination.length > 2) {
            hopMessage += ", and " + destination[2];
        }
        
        sendMessage(hopMessage, getSpawnedHostName());
        System.out.println("TestAgent: Message sent to first spawned host.");
        
        // Return to the original host to check for messages
        hop(getSpawnedHostName(), "checkMessages");
    }

    /**
     * checkMessages() is invoked when the agent returns to the original host
     * to check for and display received messages.
     */
    public void checkMessages() {
        System.out.println("TestAgent: agent(" + agentId + ") returned to original host to check messages");
        // The messages will be printed in the run() method 
    }
}
