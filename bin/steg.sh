#!/bin/bash

clear
export MAIN_DIALOG='

<window title="STEG">
  
  <notebook labels="Encrypt|Decrypt">
    
    <vbox>
    <frame Enter Secret Key>
	  <hbox>	  
	    <entry visibility="false">   
	      <default>Max 16 chars</default>
	      <variable>SKEY</variable>
	    </entry>
	   </hbox>
     </frame>
    
    <frame Select Input File>
	  <hbox>
	    <entry>
	      <variable>FILE</variable>
	    </entry>
	    <button>
	      <input file stock="gtk-open"></input>
	      <variable>FILE_BROWSE</variable>
	      <action type="fileselect">FILE</action>
	    </button>
	  </hbox>
     </frame>
      <frame Select Image File>
	  <hbox>
	    <entry>
	      <variable>IMG</variable>
	    </entry>
	    <button>
	      <input file stock="gtk-open"></input>
	      <variable>IMG_BROWSE</variable>
	      <action type="fileselect">IMG</action>
	    </button>
	  </hbox>
     </frame>
    
      <frame Select Output Directory>
	<hbox>
	  <entry accept="directory">
	    <variable>FILE_DIRECTORY</variable>
	  </entry>
	  <button>
	    <input file stock="gtk-open"></input>
	    <variable>FILE_BROWSE_DIRECTORY</variable>
	    <action type="fileselect">FILE_DIRECTORY</action>
	  </button>
	</hbox>
      </frame>
    
      <vbox>
	<button>
	  <height>20</height>
	  <width>40</width>
	  <label>Encrypt</label>
	  <action signal="clicked">clear;java stegano.UserInput "$SKEY" "$FILE" "$IMG" "$FILE_DIRECTORY"</action>
	  <variable>"flag"</variable>
	</button>
      </vbox>
    
    </vbox>
    
    <vbox>         
      <expander label="Decryption" expanded="true">
	<vbox>
	<frame Enter Secret Key>
	  <hbox>	  
	    <entry visibility="false">   
	      <default>Max 16 chars</default>
	      <variable>SSKEY</variable>
	    </entry>
	   </hbox>
     </frame>
	  <frame Choose Image>
	    <hbox>
	      <entry>
		<variable>XFILE</variable>
	      </entry>
	      <button>
		<input file stock="gtk-open"></input>
		<variable>xfile</variable>
		<action type="fileselect">XFILE</action>
	      </button>
	    </hbox>
	  </frame>
	  
	  <frame Choose Appropriate key>
	    <hbox>
	      <entry>
		<variable>KFILE</variable>
	      </entry>
	      <button>
		<input file stock="gtk-open"></input>
		<variable>kfile</variable>
		<action type="fileselect">KFILE</action>
	      </button>
	    </hbox>
	  </frame>	
	  
	 <frame Select Output Directory>
	<hbox>
	  <entry accept="directory">
	    <variable>DIR</variable>
	  </entry>
	  <button>
	    <input file stock="gtk-open"></input>
	    <variable>DIR_BROWSE</variable>
	    <action type="fileselect">DIR</action>
	  </button>
	</hbox>
      </frame>
    
	  <vbox>
	    <button>
	      <height>20</height>
	      <width>40</width>
	      <label>Decrypt</label>
	      <action signal="clicked">clear;java stegano.Decrypt "$SSKEY" "$XFILE" "$KFILE" "$DIR"</action>
	    </button>
	  </vbox>
	 </vbox>
      </expander>
    
    </vbox>
   
   </notebook>
   
</window>

'
gtkdialog --program=MAIN_DIALOG
