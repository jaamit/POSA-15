package vandy.mooc;

import java.util.concurrent.CyclicBarrier;

import android.location.GpsStatus.NmeaListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {
    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;
    
    /**
     * Debugging TAG used for Android logging
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    // @@ TODO - DONE you fill in here.
    /* Declare an array of Handlers using the enum PingPong
     * One for Ping and other for Pong.
     */
    private Handler[] mHandlers = new Handler[PingPong.values().length];

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    // @@ TODO - DONE you fill in here.
    private CyclicBarrier mCyclicBarrier = new CyclicBarrier(PingPong.values().length);

    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {
        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         */
        private PingPong mMyType;

        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
            // @@ TODO - DONE you fill in here.
        	this.mMyType = myType;
        	mIterationsCompleted = 0;
        	Log.d(TAG,
        			"PingPongThread Constructor called for "+ myType.toString());
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            // @@ TODO - DONE you fill in here.
        	/*
        	 * The code is common for both the handler threads and based on the
        	 * thread (PING or PONG) need to create the specific Handler.
        	 */
        	if(mMyType.equals(PingPong.PING)) {
        		mHandlers[PingPong.PING.ordinal()] = new Handler(this);
            	Log.d(TAG,
            			"onLooperPrepared called for PING Thread");
        	}
        	else {
        		mHandlers[PingPong.PONG.ordinal()] = new Handler(this);
            	Log.d(TAG,
            			"onLooperPrepared called for PONG Thread");
        	}

            try {
                // Wait for both Threads to initialize their Handlers.
                // @@ TODO - DONE you fill in here.
            	mCyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Start the PING_THREAD first by (1) creating a Message
            // where the PING Handler is the "target" and the PONG
            // Handler is the "obj" to use for the reply and (2)
            // sending the Message to the PING_THREAD's Handler.
            // @@ TODO - DONE you fill in here.
            if(PingPong.PING == mMyType) {
            	Log.d(TAG,
            			"Send the initial message in the PING Thread");
            	Handler pingHandler = mHandlers[PingPong.PING.ordinal()];
            	Handler pongHandler = mHandlers[PingPong.PONG.ordinal()];
            	
            	Message msg = pingHandler.obtainMessage();
            	//Message msg = Message.obtain();
            	msg.setTarget(pingHandler);
            	msg.obj = pongHandler;
            	pingHandler.sendMessage(msg);
            }
        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
        	Handler dst = (Handler) reqMsg.obj;
        	Log.d(TAG,
        			"Entered " + this.getName() + " Thread"); 
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - DONE you fill in here, replacing "true" with the
            // appropriate code.
        	if(mIterationsCompleted < mMaxIterations) {
        		mIterationsCompleted++;
        		/* What to print here? */
        		if(mHandlers[PingPong.PING.ordinal()] == reqMsg.getTarget()) {
        			// PING Thread
        			mOutputStrategy.print("PING"+"("+ mIterationsCompleted +")\n");
                	Log.d(TAG,
                			"Num Iterations for PING Thread -> " + mIterationsCompleted);        			
        		}
        		else {
        			// PONG Thread
        			mOutputStrategy.print("PONG"+"(" + mIterationsCompleted +")\n");
                	Log.d(TAG,
                			"Num Iterations for PONG Thread -> " + mIterationsCompleted);
        		}
            }
        	else {
                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - DONE you fill in here.
            	Log.d(TAG,
            			this.getName() + " Thread Iterations " + mIterationsCompleted + " QUIT Now "); 
            	quit();
        	}
 
        	
        	Log.d(TAG,
        			"Sending message in  " + this.getName() + " Thread"); 

            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - DONE you fill in here.
        	if(dst != null) {
        	Message msg = Message.obtain();
        	msg.obj = reqMsg.getTarget();
        	msg.setTarget((Handler)reqMsg.obj);
        	        	
            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            // @@ TODO - DONE you fill in here.
        	msg.getTarget().sendMessage(msg);
        	}

        	return true;
        }
    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!");
       
        // Create the ping and pong threads.
        // @@ TODO - DONE you fill in here.
        /*
        PingPongThread pingThread = new PingPongThread(PingPong.PING);
        PingPongThread pongThread = new PingPongThread(PingPong.PONG);
        
        pingThread.start();
        pongThread.start();
        
        try {
            System.out.println("before pingThread.join");
            pingThread.join();
            System.out.println("after pingThread.join");            
            pongThread.join();
            System.out.println("after pongThread.join");
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        */
        
        
        PingPongThread[] pingPongThreads = new PingPongThread[PingPong.values().length];
        int index = 0;
        for(PingPong p : PingPong.values()) {
        	pingPongThreads[index] = new PingPongThread(p);
        	pingPongThreads[index].start();
        	index++;
        }
        
        // Start ping and pong threads, which cause their Looper to
        // loop.
        // @@ TODO - DONE you fill in here.
        /* Started the two threads along with the creation above */


        // Barrier synchronization to wait for all work to be done
        // before exiting play().
        // @@ TODO - DONE you fill in here.
        try {
        	for(int i = 0; i< PingPong.values().length; i++) {
        		pingPongThreads[i].join();
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        // Let the user know we're done.
        mOutputStrategy.print("Done!");
    }
}
