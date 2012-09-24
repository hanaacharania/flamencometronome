/* *********************************************************************** *
 * project: org.jcompas.*
 * RelojRunner.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           :                                                       *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.jcompas.control;

import org.jcompas.model.JCompasGlobal;
import org.jcompas.view.Reloj;

/**
 * @author thibautd
 */
public class RelojRunner implements InfinitePlayer {
	private final Thread thread;
	private final InternRunnable runner;

	public RelojRunner(final Reloj reloj) {
		runner = new InternRunnable( reloj );
		thread = new Thread( runner );
	}

	// /////////////////////////////////////////////////////////////////////////
	// interface
	// /////////////////////////////////////////////////////////////////////////
	@Override
	public void start(final long startTime, final long compasLengthMilli) {
		runner.setStartInfo( startTime , compasLengthMilli );
		thread.start();
	}

	@Override
	public void stop() {
		thread.stop();
	}

	// /////////////////////////////////////////////////////////////////////////
	// actual stuff
	// /////////////////////////////////////////////////////////////////////////
	private static class InternRunnable implements Runnable {
		private final Reloj reloj;
		private long period = -1;
		private final int defaultFps = 60;
		private long startTime = -1;

		public InternRunnable(final Reloj reloj) {
			this.reloj = reloj;
		}

		public void setStartInfo( final long startTime , final long period ) {
			this.startTime = startTime;
			this.period = period;
		}


		@Override
		public void run() {
			if (period < 1) throw new IllegalStateException( "invalid period: "+period+" ms." );

			final long timeStep = 1000 / defaultFps;
			long last = startTime;
			while (true) {
				try {
					Thread.sleep( Math.max( last + timeStep - System.currentTimeMillis() , 1 ) );
				} catch (InterruptedException e) {
					JCompasGlobal.notifyException( "" , e );
				}
				last = System.currentTimeMillis();
				reloj.notifyCompasFraction(
						((double) ( System.currentTimeMillis() - startTime) / period) % 1 );
			}
		}
	}
}

