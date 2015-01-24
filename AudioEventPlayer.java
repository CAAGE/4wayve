import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;
import java.io.File;

public class AudioEventPlayer {
	
	public static int MAXIMPLEMENTEDINSTRUMENTINDEX = 3;
	
	public static void startNote(int instrument, int note)  throws MidiUnavailableException,InvalidMidiDataException {
        Receiver rcvr = MidiSystem.getReceiver();
        int notevalue = 0;
        int bank = 0;
        if(instrument == 0){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 60;
            } if(note == 1){
                notevalue = 64;
            } if(note == 2){
                notevalue = 67;
            }
        }
        if(instrument == 1){
            bank = 9;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,0,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 36;
            }
            if(note == 1){
                notevalue = 38;
            }
            if(note == 2){
                notevalue = 49;
            }
        }
        if(instrument == 2){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,33,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 36;
            }
            if(note == 1){
                notevalue = 40;
            }
            if(note == 2){
                notevalue = 43;
            }
        }
        if(instrument == 3){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,26,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 48;
            }
            if(note == 1){
                notevalue = 52;
            }
            if(note == 2){
                notevalue = 55;
            }
        }
        if(instrument == -1){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 115, 0);
            rcvr.send(midiinstrument,-1);
            notevalue = 60;
        }
        ShortMessage midinote = new ShortMessage();
        midinote.setMessage(ShortMessage.NOTE_ON, bank, notevalue, 93);
        rcvr.send(midinote,-1);
                   
	}

	public static void endNote(int instrument, int note) throws MidiUnavailableException,InvalidMidiDataException {
        Receiver rcvr = MidiSystem.getReceiver();
        try{
        int notevalue = 0;
        if(instrument == 0){
            if(note == 0){
                notevalue = 60;
            } if(note == 1){
                notevalue = 64;
            } if(note == 2){
                notevalue = 67;
            }
        }
        if(instrument == 2){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,33,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 36;
            }
            if(note == 1){
                notevalue = 40;
            }
            if(note == 2){
                notevalue = 43;
            }
        }
        if(instrument == 3){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,26,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 48;
            }
            if(note == 1){
                notevalue = 52;
            }
            if(note == 2){
                notevalue = 55;
            }
        }
        if(instrument == -1){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 115, 0);
            rcvr.send(midiinstrument,-1);
            notevalue = 60;
        }
        ShortMessage midinote = new ShortMessage();
	    midinote.setMessage(ShortMessage.NOTE_OFF,0,notevalue,93);
        rcvr.send(midinote,-1);
        
        } catch (Exception e) {}
	}
	
	/**
	 * This will write events to a file.
	 * @param location The file to write to.
	 * @param instruments The instruments in the file.
	 * @param startTimes The start times of each event.
	 * @param endTimes The end times of each instrument event.
	 * @throws IOException If there is a problem writing.
	 */
	public static void saveEventFile(File location, int[] instruments, List<List<Long>> startTimes, List<List<Long>> endTimes) throws IOException{
		DataOutputStream toWrite = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(location)));
		toWrite.writeInt(instruments[0]);
		toWrite.writeInt(instruments[1]);
		toWrite.writeInt(instruments[2]);
		toWrite.writeInt(instruments[3]);
		for(int i = 0; i<12; i++){
			List<Long> stimes = startTimes.get(i);
			List<Long> etimes = endTimes.get(i);
			toWrite.writeInt(stimes.size());
			for(Long cur : stimes){
				toWrite.writeLong(cur);
			}
			toWrite.writeInt(etimes.size());
			for(Long cur : etimes){
				toWrite.writeLong(cur);
			}
		}
		toWrite.close();
	}
	
	/**
	 * This will read events from a file.
	 * @param location The file to read from.
	 * @return {instruments, startTimes, endTimes}.
	 * @throws IOException If there is a problem reading.
	 */
	public static Object[] readEventFile(File location) throws IOException{
		DataInputStream toRead = new DataInputStream(new BufferedInputStream(new FileInputStream(location)));
		int[] instruments = new int[4];
		instruments[0] = toRead.readInt();
		instruments[1] = toRead.readInt();
		instruments[2] = toRead.readInt();
		instruments[3] = toRead.readInt();
		List<List<Long>> startTimes = new ArrayList<>();
		List<List<Long>> endTimes = new ArrayList<>();
		for(int i = 0; i<12; i++){
			List<Long> stimes = new ArrayList<>();
			List<Long> etimes = new ArrayList<>();
			int slen = toRead.readInt();
			for(int j = 0; j<slen; j++){
				stimes.add(toRead.readLong());
			}
			int elen = toRead.readInt();
			for(int j = 0; j<elen; j++){
				etimes.add(toRead.readLong());
			}
			startTimes.add(stimes);
			endTimes.add(etimes);
		}
		toRead.close();
		return new Object[]{instruments, startTimes, endTimes};
	}
	
	public static void main(String[] args) throws MidiUnavailableException,InvalidMidiDataException{
        Sequencer sequencer;
	    // Get default sequencer.
	    sequencer = MidiSystem.getSequencer(); 
	    if (sequencer == null) {
	        // Error -- sequencer device is not supported.
	        // Inform user and return...	    	
	    } else {
	        // Acquire resources and make operational.
            sequencer.open();
            startNote(3,0);
		    try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(3,0);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            startNote(0,1);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(0,1);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            startNote(1,2);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(1,2);
        }   
    }
}
