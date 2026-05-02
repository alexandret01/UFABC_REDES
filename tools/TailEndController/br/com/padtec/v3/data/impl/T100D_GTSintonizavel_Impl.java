package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.TrpOTNTerminalSintonizavel;

public class T100D_GTSintonizavel_Impl extends T100D_GT_Impl
  implements TrpOTNTerminalSintonizavel {
  private static final long serialVersionUID = 1L;

  public T100D_GTSintonizavel_Impl(SerialNumber serial)  {
    super(serial);
  }
  
  public String toStringDetalhed(){
	  StringBuilder builder = new StringBuilder();
	  builder.append(this.toString());
	  builder.append(":\n");
	  builder.append("Slot: ");
	  builder.append(getSlot());
	  builder.append("\nPin: ");
	  builder.append(this.getPin());
	  builder.append(" dBm");
	  builder.append("\nPout: ");
	  builder.append(this.getPout());
	  builder.append(" dBm");
	  builder.append("\nOptical Transport Unit:");
	  builder.append("\n\tBackward Error Indication - BEI: ");
	  builder.append(getBei_OTUk());
	  builder.append("\n\tBit Interleaved Parity (Level 8): ");
	  builder.append(getBip8_OTUk());
	  builder.append("\n\tBip8Rate: ");
	  builder.append(getBIP8Rate_OTUk());
	  builder.append("\nOptical Data Unit - ODU:");
	  builder.append("\n\tStatus: ");
	  builder.append(getODUk().getStatDesc());
	  builder.append("\n\tBackward Error Indication - BEI: ");
	  builder.append(getBei_ODUk());
	  builder.append("\n\tBit Interleaved Parity (Level 8): ");
	  builder.append(getBip8_ODUk());
	  builder.append("\n\tBip8Rate: ");
	  builder.append(getBIP8Rate_ODUk());
	 
	  
	  return builder.toString();
  }
  
  
}