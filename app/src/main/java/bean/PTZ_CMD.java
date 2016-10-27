package bean;



public enum PTZ_CMD {
	ptz_up(0x01),
	ptz_down(0x02),
	ptz_left(0x03),
	ptz_right(0x04),
	ptz_lrisOpen(0x11),
	ptz_lrisClose(0x12),
	ptz_zoomTele(0x13),
	ptz_zoomWide(0x14),
	ptz_focusFar(0x15),
	ptz_focusNear(0x16),
	ptz_clearPreset(0x20),
	ptz_setPreset(0x21),
	ptz_gotoPreset(0x22),
	ptz_null(0xff)
	;
	
	private int val;
	private PTZ_CMD(int val){
		this.val = val;
	}
	public int getVal(){
		return val;
	}
	public static PTZ_CMD valueOf(int v){
		switch (v) {
		case 0x01:
			return ptz_up;
		case 0x02:
			return ptz_down;
		case 0x03:
			return ptz_left;
		case 0x04:
			return ptz_right;
		case 0x11:
			return ptz_lrisOpen;
		case 0x12:
			return ptz_lrisClose;
		case 0x13:
			return ptz_zoomTele;
		case 0x14:
			return ptz_zoomWide;
		case 0x15:
			return ptz_focusFar;
		case 0x16:
			return ptz_focusNear;
		case 0x20:
			return ptz_clearPreset;
		case 0x21:
			return ptz_setPreset;
		case 0x22:
			return ptz_gotoPreset;
		default:
			break;
		}
		return ptz_null;
		
		
	}
	
}
