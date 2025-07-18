package Mobile;

import java.lang.reflect.*;
import java.util.Objects;
import java.net.InetAddress;

/**
 * Mobile.Inject reads a given agent class from local disk, instantiates a new
 * object from it, and transfers this agent to a given destination IP where
 * the agent starts with the init( ) function.
 *
 * @author  Munehiro Fukuda
 * @version %I% %G%
 * @since   1.0
 */
public class Inject {
    // an error message to be printed if arguments passed to main( ) are wrong.
    public static String usage = 
	"usage: java -cp Mobile.jar Mobile.Inject host port agent keyword (arg1...N)";

    /**
     * main( ) read an agent byte code from the local disk, instantiates a new
     * object from it, and transfers this agent to a given desgtination IP
     * where the agent starts with init( ) function.
     * 
     * @param args consists of host name, port, agent class name, and 0 or more
     *             arguments passed to an agent to be injected.
     */
    @SuppressWarnings( "unchecked" )
    public static void main( String[] args ) {
	// verify arguments
	if ( args.length < 4 ) {
	    System.err.println( usage );
	    System.exit( -1 );
	}

	String hostname = args[0];               // args[0] = hostname
	int port = 0;
	try {
	    port = Integer.parseInt( args[1] );  // args[1] = port
	} catch ( Exception e ) {
	    e.printStackTrace( );
	    System.err.println( usage );
	    System.exit( -1 );
	}
	String agentClassName = args[2];         // args[2] = agent class name
	String keyword = args[3];                // args[3] = keyword for inverted indexing

	// if args.length >= 3, allocate arguments to be passed to this agent
	//String[] arguments = ( args.length == 3 ) ? 
	// null : new String[ args.length - 3 ];
	//if ( arguments != null )
	// for ( int i = 0; i < arguments.length; i++ )
	//	arguments[i] = args[3 + i];      // args[3, 4, ...] = arguments

	// Prepare arguments array including keyword and destinations
        String[] arguments = new String[args.length - 3];
        arguments[0] = keyword;  // First argument is always the keyword
        
        // Copy destination hosts
        for (int i = 4; i < args.length; i++) {
            arguments[i - 3] = args[i];
        }

	// read this agent's byte code
	byte[] bytecode = Agent.getByteCode( agentClassName );
	try {
	    // retrieve this agent's class 
	    AgentLoader loader = new AgentLoader( );
	    Class agentClass = loader.loadClass( agentClassName, bytecode );

	    Agent agent = null;
            //if ( arguments == null ) {
		// If there are no arguments provided for the construction
		// of the injected agent, then simply create the new instance.
	    //     agent = ( Agent )( agentClass.newInstance() );
	    // } else {
                // Otherwise, pass the additional arguments to the constructor
                // of the injected agent.
	    //  Object[] constructorArgs = new Object[]{arguments};

                // Locate this agent's constructor and instantiate the agent
	    //  Constructor agentConst 
	    //	    = agentClass.getConstructor(new Class[]{String[].class});
	    //  agent = ( Agent )( agentConst.newInstance(constructorArgs) );
	    // }

	    // constructor call to pass keyword and destinations
            if (arguments.length == 1) {
                // Only keyword provided, no destinations
                Object[] constructorArgs = new Object[]{new String[]{keyword}, hostname};
                Constructor agentConst = agentClass.getConstructor(
                    new Class[]{String[].class, String.class});
                agent = (Agent)(agentConst.newInstance(constructorArgs));
            } else {
                // Keyword and destinations provided
                Object[] constructorArgs = new Object[]{arguments, hostname};
                Constructor agentConst = agentClass.getConstructor(
                    new Class[]{String[].class, String.class});
                agent = (Agent)(agentConst.newInstance(constructorArgs));
            }

	    // let this new agent hop to hostnaem:port
	    agent.setPort( port );

	    // Set keyword for the agent
            agent.setKeyword(keyword);

	    // set the hostname of this agent to be the hostname of the machine where this agent starts.
            if (Objects.equals(hostname, "localhost")) {
                agent.setSpawnedHostName(InetAddress.getLocalHost().getHostName());
            } else {
                agent.setSpawnedHostName(hostname);
	    }

	    System.out.println("Injecting TestAgent with keyword: " + keyword);
            System.out.println("Destinations: " + Arrays.toString(arguments));
	    
	    agent.hop( hostname, "init" );

	} catch ( Exception e ) {
	    e.printStackTrace( );
	    System.exit( -1 );
	}
	

    }
}
