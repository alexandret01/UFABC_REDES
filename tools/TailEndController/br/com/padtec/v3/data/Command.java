package br.com.padtec.v3.data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.Msg;


public class Command implements Serializable  {
	private static final long serialVersionUID = 10L;
	public static final int TRP_SET_LASER_ON = 1;
	public static final int TRP_SET_LASER_OFF = 2;
	public static final int TRP_RESET_COUNT = 3;
	public static final int TRP_SET_LASER2_ON = 15;
	public static final int TRP_SET_LASER2_OFF = 16;
	public static final int TRP_SET_TAXA = 4;
	public static final int TRP_ODU_SAPI_TX = 5;
	public static final int TRP_ODU_DAPI_TX = 6;
	public static final int TRP_ODU_SAPI_RX = 7;
	public static final int TRP_ODU_DAPI_RX = 8;
	public static final int TRP_FEC_TX_OFF = 9;
	public static final int TRP_FEC_TX_ON = 10;
	public static final int TRP_FEC_RX_OFF = 11;
	public static final int TRP_FEC_RX_ON = 12;
	public static final int TRP_TIM_ACT_ON = 13;
	public static final int TRP_TIM_ACT_OFF = 14;
	public static final int TRP_OTU_SAPI_TX = 101;
	public static final int TRP_OTU_DAPI_TX = 102;
	public static final int TRP_OTU_SAPI_RX = 103;
	public static final int TRP_OTU_DAPI_RX = 104;
	public static final int TRP_SET_TAXA_MODE = 105;
	public static final int TRP_OTU_SAPI2_TX = 106;
	public static final int TRP_OTU_DAPI2_TX = 107;
	public static final int TRP_OTU_SAPI2_RX = 108;
	public static final int TRP_OTU_DAPI2_RX = 109;
	public static final int TRP_FEC2_TX_OFF = 110;
	public static final int TRP_FEC2_TX_ON = 111;
	public static final int TRP_FEC2_RX_OFF = 112;
	public static final int TRP_FEC2_RX_ON = 113;
	public static final int TRP_AUTOLASEROFF_OFF = 114;
	public static final int TRP_AUTOLASEROFF_ON = 115;
	public static final int TRP_AUTOLASEROFF2_OFF = 116;
	public static final int TRP_AUTOLASEROFF2_ON = 117;
	public static final int MUX_SFP1_ONOFF = 118;
	public static final int MUX_SFP2_ONOFF = 119;
	public static final int MUX_SFP3_ONOFF = 120;
	public static final int MUX_SFP4_ONOFF = 121;
	public static final int COMB_SFP_ON = 122;
	public static final int COMB_SFP_OFF = 123;
	public static final int COMB_CLIENT_PORT_CONF = 124;
	public static final int COMB_AUTOLASEROFF2_OFF = 125;
	public static final int COMB_AUTOLASEROFF2_ON = 126;
	public static final int TRP_REG_AUTOLASEROFF2_OFF = 127;
	public static final int TRP_REG_AUTOLASEROFF2_ON = 128;
	public static final int COMB_AUTOSENSE_ON = 129;
	public static final int COMB_AUTOSENSE_OFF = 130;
	public static final int TRP_SET_MAX_FRAME_SIZE = 131;
	public static final int TRP_SET_J0_RX = 132;
	public static final int TRP_SET_J0_TX = 133;
	public static final int TRP_SET_TIM_ACT_RS_ON = 134;
	public static final int TRP_SET_TIM_ACT_RS_OFF = 135;
	public static final int TRP_SET_MODO_J0_TERMINADO_ON = 136;
	public static final int TRP_SET_MODO_J0_TERMINADO_OFF = 137;
	public static final int COMB_FPGA_RESET = 138;
	public static final int AMP_SET_LASER_ON = 18;
	public static final int AMP_SET_LASER_OFF = 19;
	public static final int AMP_SET_AGC_GAIN = 20;
	public static final int OPS_SWITCH = 21;
	public static final int OPS_LOCK = 22;
	public static final int OPS_AUTO = 23;
	public static final int SW8_AUTO_MANUAL = 24;
	public static final int SW8_SET_CHANNEL = 25;
	public static final int SW8_SWITCH = 26;
	public static final int SUP_CONF = 40;
	public static final int SUP_BOOT = 41;
	public static final int SUP_ADDR = 42;
	public static final int SUP_NET = 43;
	public static final int SUP_CLOCK = 44;
	public static final int SUP_LOCK = 45;
	public static final int SUP_UNBLOCK = 46;
	public static final int SUP_CONN_CLOSE = 47;
	public static final int SUP_HISTORY = 48;
	public static final int SUP_PERFORMANCE = 49;
	public static final int SUP_CONT_RESET = 51;
	public static final int SUP_RACK_CONF = 150;
	public static final int SUP_RACK_CONF_SPVL = 52;
	public static final int SUP_HARD_BOOT = 53;
	public static final int SUP_SET_SEGMENTATION = 54;
	public static final int SHK_COMMAND = 50;
	public static final int FAN_SET_MAX_TEMP = 200;
	public static final int FAN_SET_VELOCITY = 201;
	public static final int TL1_COMMAND = 300;
	public static final int AMP_RESTART = 301;
	public static final int AMP_TURN_ON_EYE_PROTECTION = 302;
	public static final int AMP_TURN_ON_APR = 303;
	public static final int AMP_TURN_ON_AGC = 304;
	public static final int AMP_EXPORT_CONFIG = 306;
	public static final int AMP_TURN_LASER_ON_OFF = 307;
	public static final int AMP_SAVE_CONFIG_LIMIAR_POTOUT = 308;
	public static final int AMP_SAVE_CONFIG_LIMIAR_POTIN = 309;
	public static final int AMP_SAVE_CONFIG_LIMIAR_CORR = 310;
	public static final int AMP_SAVE_CONFIG_LIMIAR_POT = 311;
	public static final int AMP_SAVE_CONFIG_GANHO_AGC = 312;
	public static final int AMP_SAVE_CONFIG_POTL1 = 313;
	public static final int AMP_SAVE_CONFIG_POTL2 = 314;
	public static final int AMP_SAVE_CONFIG_POTL3 = 315;
	public static final int AMP_SAVE_CONFIG_POTL4 = 316;
	public static final int AMP_SAVE_IP = 318;
	public static final int AMP_SET_RW = 319;
	public static final int AMP_GET_RW = 320;

	public static final int AMP_GET_RO = 322;
	public static final int AMP_TRAP_IP = 323;
	public static final int AMP_TRAP_PORTA = 324;
	public static final int AMP_TRAP_RW_COMMUNITY = 325;
	public static final int AMP_TRAP_RO_COMMUNITY = 326;
	public static final int AMP_IP_APR = 328;
	public static final int SUP_HISRESTART = 329;
	public static final int ROADM_SET_CHANNEL_CONFIG_00 = 330;
	public static final int ROADM_SET_CHANNEL_CONFIG_01 = 331;
	public static final int ROADM_SET_CHANNEL_CONFIG_02 = 332;
	public static final int ROADM_SET_CHANNEL_CONFIG_03 = 333;
	public static final int ROADM_SET_CHANNEL_CONFIG_04 = 334;
	public static final int ROADM_SET_CHANNEL_CONFIG_05 = 335;
	public static final int ROADM_SET_CHANNEL_CONFIG_06 = 336;
	public static final int ROADM_SET_CHANNEL_CONFIG_07 = 337;
	public static final int ROADM_SET_CHANNEL_CONFIG_08 = 338;
	public static final int ROADM_SET_CHANNEL_CONFIG_09 = 339;
	public static final int ROADM_SET_CHANNEL_CONFIG_10 = 340;
	public static final int ROADM_SET_CHANNEL_CONFIG_11 = 341;
	public static final int ROADM_SET_CHANNEL_CONFIG_12 = 342;
	public static final int ROADM_SET_CHANNEL_CONFIG_13 = 343;
	public static final int ROADM_SET_CHANNEL_CONFIG_14 = 344;
	public static final int ROADM_SET_CHANNEL_CONFIG_15 = 345;
	public static final int ROADM_SET_CHANNEL_CONFIG_16 = 346;
	public static final int ROADM_SET_CHANNEL_CONFIG_17 = 347;
	public static final int ROADM_SET_CHANNEL_CONFIG_18 = 348;
	public static final int ROADM_SET_CHANNEL_CONFIG_19 = 349;
	public static final int ROADM_SET_CHANNEL_CONFIG_20 = 350;
	public static final int ROADM_SET_CHANNEL_CONFIG_21 = 351;
	public static final int ROADM_SET_CHANNEL_CONFIG_22 = 352;
	public static final int ROADM_SET_CHANNEL_CONFIG_23 = 353;
	public static final int ROADM_SET_CHANNEL_CONFIG_24 = 354;
	public static final int ROADM_SET_CHANNEL_CONFIG_25 = 355;
	public static final int ROADM_SET_CHANNEL_CONFIG_26 = 356;
	public static final int ROADM_SET_CHANNEL_CONFIG_27 = 357;
	public static final int ROADM_SET_CHANNEL_CONFIG_28 = 358;
	public static final int ROADM_SET_CHANNEL_CONFIG_29 = 359;
	public static final int ROADM_SET_CHANNEL_CONFIG_30 = 360;
	public static final int ROADM_SET_CHANNEL_CONFIG_31 = 361;
	public static final int ROADM_SET_CHANNEL_CONFIG_32 = 362;
	public static final int ROADM_SET_CHANNEL_CONFIG_33 = 363;
	public static final int ROADM_SET_CHANNEL_CONFIG_34 = 364;
	public static final int ROADM_SET_CHANNEL_CONFIG_35 = 365;
	public static final int ROADM_SET_CHANNEL_CONFIG_36 = 366;
	public static final int ROADM_SET_CHANNEL_CONFIG_37 = 367;
	public static final int ROADM_SET_CHANNEL_CONFIG_38 = 368;
	public static final int ROADM_SET_CHANNEL_CONFIG_39 = 369;
	public static final int ROADM_SET_LOOP_STATE = 370;
	public static final int ROADM_SET_ATENUATION_THRESHOLD = 371;
	public static final int ROADM_SET_GROUP = 372;
	public static final int ROADM_SET_SIDE = 373;
	public static final int ROADM_SET_NEXT_ROADM_BOARD = 374;
	public static final int ROADM_SET_VOA_ATT_DEMUX = 375;
	public static final int TRP_ITU_CHANNEL_LADO1 = 400;
	public static final int TRP_ITU_CHANNEL_LADO2 = 401;
	public static final int AMP_AGC_ON = 402;
	public static final int AMP_AGC_OFF = 403;
	public static final int AMP_EYE_ON = 404;
	public static final int AMP_EYE_OFF = 405;
	public static final int ROADM_SET_VOA_ATT_ADD_00 = 410;
	public static final int ROADM_SET_VOA_ATT_ADD_01 = 411;
	public static final int ROADM_SET_VOA_ATT_ADD_02 = 412;
	public static final int ROADM_SET_VOA_ATT_ADD_03 = 413;
	public static final int ROADM_SET_VOA_ATT_ADD_04 = 414;
	public static final int ROADM_SET_VOA_ATT_ADD_05 = 415;
	public static final int ROADM_SET_VOA_ATT_ADD_06 = 416;
	public static final int ROADM_SET_VOA_ATT_ADD_07 = 417;
	public static final int ROADM_SET_VOA_ATT_ADD_08 = 418;
	public static final int ROADM_SET_VOA_ATT_ADD_09 = 419;
	public static final int ROADM_SET_VOA_ATT_ADD_10 = 420;
	public static final int ROADM_SET_VOA_ATT_ADD_11 = 421;
	public static final int ROADM_SET_VOA_ATT_ADD_12 = 422;
	public static final int ROADM_SET_VOA_ATT_ADD_13 = 423;
	public static final int ROADM_SET_VOA_ATT_ADD_14 = 424;
	public static final int ROADM_SET_VOA_ATT_ADD_15 = 425;
	public static final int ROADM_SET_VOA_ATT_ADD_16 = 426;
	public static final int ROADM_SET_VOA_ATT_ADD_17 = 427;
	public static final int ROADM_SET_VOA_ATT_ADD_18 = 428;
	public static final int ROADM_SET_VOA_ATT_ADD_19 = 429;
	public static final int ROADM_SET_VOA_ATT_ADD_20 = 430;
	public static final int ROADM_SET_VOA_ATT_ADD_21 = 431;
	public static final int ROADM_SET_VOA_ATT_ADD_22 = 432;
	public static final int ROADM_SET_VOA_ATT_ADD_23 = 433;
	public static final int ROADM_SET_VOA_ATT_ADD_24 = 434;
	public static final int ROADM_SET_VOA_ATT_ADD_25 = 435;
	public static final int ROADM_SET_VOA_ATT_ADD_26 = 436;
	public static final int ROADM_SET_VOA_ATT_ADD_27 = 437;
	public static final int ROADM_SET_VOA_ATT_ADD_28 = 438;
	public static final int ROADM_SET_VOA_ATT_ADD_29 = 439;
	public static final int ROADM_SET_VOA_ATT_ADD_30 = 440;
	public static final int ROADM_SET_VOA_ATT_ADD_31 = 441;
	public static final int ROADM_SET_VOA_ATT_ADD_32 = 442;
	public static final int ROADM_SET_VOA_ATT_ADD_33 = 443;
	public static final int ROADM_SET_VOA_ATT_ADD_34 = 444;
	public static final int ROADM_SET_VOA_ATT_ADD_35 = 445;
	public static final int ROADM_SET_VOA_ATT_ADD_36 = 446;
	public static final int ROADM_SET_VOA_ATT_ADD_37 = 447;
	public static final int ROADM_SET_VOA_ATT_ADD_38 = 448;
	public static final int ROADM_SET_VOA_ATT_ADD_39 = 449;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_00 = 450;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_01 = 451;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_02 = 452;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_03 = 453;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_04 = 454;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_05 = 455;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_06 = 456;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_07 = 457;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_08 = 458;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_09 = 459;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_10 = 460;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_11 = 461;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_12 = 462;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_13 = 463;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_14 = 464;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_15 = 465;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_16 = 466;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_17 = 467;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_18 = 468;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_19 = 469;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_20 = 470;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_21 = 471;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_22 = 472;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_23 = 473;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_24 = 474;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_25 = 475;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_26 = 476;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_27 = 477;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_28 = 478;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_29 = 479;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_30 = 480;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_31 = 481;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_32 = 482;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_33 = 483;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_34 = 484;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_35 = 485;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_36 = 486;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_37 = 487;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_38 = 488;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_39 = 489;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_00 = 490;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_01 = 491;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_02 = 492;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_03 = 493;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_04 = 494;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_05 = 495;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_06 = 496;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_07 = 497;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_08 = 498;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_09 = 499;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_10 = 500;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_11 = 501;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_12 = 502;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_13 = 503;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_14 = 504;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_15 = 505;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_16 = 506;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_17 = 507;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_18 = 508;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_19 = 509;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_20 = 510;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_21 = 511;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_22 = 512;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_23 = 513;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_24 = 514;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_25 = 515;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_26 = 516;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_27 = 517;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_28 = 518;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_29 = 519;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_30 = 520;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_31 = 521;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_32 = 522;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_33 = 523;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_34 = 524;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_35 = 525;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_36 = 526;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_37 = 527;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_38 = 528;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_39 = 529;
	public static final int ROADM_SET_AUTO_SWITCHING_ON_ALL_CHANNELS = 530;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_00 = 531;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_01 = 532;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_02 = 533;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_03 = 534;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_04 = 535;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_05 = 536;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_06 = 537;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_07 = 538;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_08 = 539;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_09 = 540;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_10 = 541;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_11 = 542;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_12 = 543;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_13 = 544;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_14 = 545;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_15 = 546;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_16 = 547;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_17 = 548;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_18 = 549;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_19 = 550;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_20 = 551;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_21 = 552;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_22 = 553;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_23 = 554;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_24 = 555;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_25 = 556;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_26 = 557;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_27 = 558;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_28 = 559;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_29 = 560;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_30 = 561;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_31 = 562;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_32 = 563;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_33 = 564;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_34 = 565;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_35 = 566;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_36 = 567;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_37 = 568;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_38 = 569;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_39 = 570;
	public static final int ROADM_SET_AUTO_SWITCHING_OFF_ALL_CHANNELS = 571;
	public static final int ROADM_SET_MANUAL_SWITCHING_00 = 572;
	public static final int ROADM_SET_MANUAL_SWITCHING_01 = 573;
	public static final int ROADM_SET_MANUAL_SWITCHING_02 = 574;
	public static final int ROADM_SET_MANUAL_SWITCHING_03 = 575;
	public static final int ROADM_SET_MANUAL_SWITCHING_04 = 576;
	public static final int ROADM_SET_MANUAL_SWITCHING_05 = 577;
	public static final int ROADM_SET_MANUAL_SWITCHING_06 = 578;
	public static final int ROADM_SET_MANUAL_SWITCHING_07 = 579;
	public static final int ROADM_SET_MANUAL_SWITCHING_08 = 580;
	public static final int ROADM_SET_MANUAL_SWITCHING_09 = 581;
	public static final int ROADM_SET_MANUAL_SWITCHING_10 = 582;
	public static final int ROADM_SET_MANUAL_SWITCHING_11 = 583;
	public static final int ROADM_SET_MANUAL_SWITCHING_12 = 584;
	public static final int ROADM_SET_MANUAL_SWITCHING_13 = 585;
	public static final int ROADM_SET_MANUAL_SWITCHING_14 = 586;
	public static final int ROADM_SET_MANUAL_SWITCHING_15 = 587;
	public static final int ROADM_SET_MANUAL_SWITCHING_16 = 588;
	public static final int ROADM_SET_MANUAL_SWITCHING_17 = 589;
	public static final int ROADM_SET_MANUAL_SWITCHING_18 = 590;
	public static final int ROADM_SET_MANUAL_SWITCHING_19 = 591;
	public static final int ROADM_SET_MANUAL_SWITCHING_20 = 592;
	public static final int ROADM_SET_MANUAL_SWITCHING_21 = 593;
	public static final int ROADM_SET_MANUAL_SWITCHING_22 = 594;
	public static final int ROADM_SET_MANUAL_SWITCHING_23 = 595;
	public static final int ROADM_SET_MANUAL_SWITCHING_24 = 596;
	public static final int ROADM_SET_MANUAL_SWITCHING_25 = 597;
	public static final int ROADM_SET_MANUAL_SWITCHING_26 = 598;
	public static final int ROADM_SET_MANUAL_SWITCHING_27 = 599;
	public static final int ROADM_SET_MANUAL_SWITCHING_28 = 600;
	public static final int ROADM_SET_MANUAL_SWITCHING_29 = 601;
	public static final int ROADM_SET_MANUAL_SWITCHING_30 = 602;
	public static final int ROADM_SET_MANUAL_SWITCHING_31 = 603;
	public static final int ROADM_SET_MANUAL_SWITCHING_32 = 604;
	public static final int ROADM_SET_MANUAL_SWITCHING_33 = 605;
	public static final int ROADM_SET_MANUAL_SWITCHING_34 = 606;
	public static final int ROADM_SET_MANUAL_SWITCHING_35 = 607;
	public static final int ROADM_SET_MANUAL_SWITCHING_36 = 608;
	public static final int ROADM_SET_MANUAL_SWITCHING_37 = 609;
	public static final int ROADM_SET_MANUAL_SWITCHING_38 = 610;
	public static final int ROADM_SET_MANUAL_SWITCHING_39 = 611;
	public static final int ROADM_SET_MANUAL_SWITCHING_ALL_CHANNELS = 612;
	public static final int ROADM_SET_POT_00 = 613;
	public static final int ROADM_SET_POT_01 = 614;
	public static final int ROADM_SET_POT_02 = 615;
	public static final int ROADM_SET_POT_03 = 616;
	public static final int ROADM_SET_POT_04 = 617;
	public static final int ROADM_SET_POT_05 = 618;
	public static final int ROADM_SET_POT_06 = 619;
	public static final int ROADM_SET_POT_07 = 620;
	public static final int ROADM_SET_POT_08 = 621;
	public static final int ROADM_SET_POT_09 = 622;
	public static final int ROADM_SET_POT_10 = 623;
	public static final int ROADM_SET_POT_11 = 624;
	public static final int ROADM_SET_POT_12 = 625;
	public static final int ROADM_SET_POT_13 = 626;
	public static final int ROADM_SET_POT_14 = 627;
	public static final int ROADM_SET_POT_15 = 628;
	public static final int ROADM_SET_POT_16 = 629;
	public static final int ROADM_SET_POT_17 = 630;
	public static final int ROADM_SET_POT_18 = 631;
	public static final int ROADM_SET_POT_19 = 632;
	public static final int ROADM_SET_POT_20 = 633;
	public static final int ROADM_SET_POT_21 = 634;
	public static final int ROADM_SET_POT_22 = 635;
	public static final int ROADM_SET_POT_23 = 636;
	public static final int ROADM_SET_POT_24 = 637;
	public static final int ROADM_SET_POT_25 = 638;
	public static final int ROADM_SET_POT_26 = 639;
	public static final int ROADM_SET_POT_27 = 640;
	public static final int ROADM_SET_POT_28 = 641;
	public static final int ROADM_SET_POT_29 = 642;
	public static final int ROADM_SET_POT_30 = 643;
	public static final int ROADM_SET_POT_31 = 644;
	public static final int ROADM_SET_POT_32 = 645;
	public static final int ROADM_SET_POT_33 = 646;
	public static final int ROADM_SET_POT_34 = 647;
	public static final int ROADM_SET_POT_35 = 648;
	public static final int ROADM_SET_POT_36 = 649;
	public static final int ROADM_SET_POT_37 = 650;
	public static final int ROADM_SET_POT_38 = 651;
	public static final int ROADM_SET_POT_39 = 652;
	public static final int ROADM_SET_POT_ALL_CHANNELS = 653;
	public static final int CLOSED_LOOP_MODE_ON = 654;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_1 = 655;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_2 = 656;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_3 = 657;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_4 = 658;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_5 = 659;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_6 = 660;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_7 = 661;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_8 = 662;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_9 = 663;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_10 = 664;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_11 = 665;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_12 = 666;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_13 = 667;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_14 = 668;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_15 = 669;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_16 = 670;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_17 = 671;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_18 = 672;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_19 = 673;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_20 = 674;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_21 = 675;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_22 = 676;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_23 = 677;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_24 = 678;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_25 = 679;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_26 = 680;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_27 = 681;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_28 = 682;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_29 = 683;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_30 = 684;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_31 = 685;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_32 = 686;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_33 = 687;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_34 = 688;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_35 = 689;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_36 = 690;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_37 = 691;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_38 = 692;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_39 = 693;
	public static final int CLOSED_LOOP_MODE_ON_CHANNEL_40 = 694;
	public static final int CLOSED_LOOP_MODE_OFF = 713;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_1 = 714;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_2 = 715;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_3 = 716;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_4 = 717;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_5 = 718;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_6 = 719;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_7 = 720;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_8 = 721;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_9 = 722;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_10 = 723;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_11 = 724;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_12 = 725;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_13 = 726;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_14 = 727;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_15 = 728;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_16 = 729;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_17 = 730;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_18 = 731;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_19 = 732;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_20 = 733;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_21 = 734;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_22 = 735;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_23 = 736;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_24 = 737;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_25 = 738;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_26 = 739;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_27 = 740;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_28 = 741;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_29 = 742;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_30 = 743;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_31 = 744;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_32 = 745;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_33 = 746;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_34 = 747;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_35 = 748;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_36 = 749;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_37 = 750;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_38 = 751;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_39 = 752;
	public static final int CLOSED_LOOP_MODE_OFF_CHANNEL_40 = 753;
	public static final int ROADM_SET_ROAM_CALIBRATE = 754;
	public static final int ROADM_SET_CHANNEL_CONFIG_ALL = 755;
	public static final int ROADM_SET_VOA_ATT_ADD_ALL = 756;
	public static final int ROADM_SET_VOA_ATT_EXPRESS_ALL = 757;
	public static final int ROADM_SET_DEMUX_CALIBRATION_RESTART = 758;
	public static final int FANG8_SET_SENSORS_TEMP_THR = 700;
	public static final int FANG8_SET_SENSOR1_TEMP_THR = 701;
	public static final int FANG8_SET_SENSOR2_TEMP_THR = 702;
	public static final int FANG8_SET_COOLERS_SPEED = 703;
	public static final int FANG8_SET_COOLER1_SPEED = 704;
	public static final int FANG8_SET_COOLER2_SPEED = 705;
	public static final int FANG8_SET_COOLER3_SPEED = 706;
	public static final int FANG8_SET_COOLER4_SPEED = 707;
	public static final int FANG8_SET_COOLER5_SPEED = 708;
	public static final int FANG8_SET_COOLER6_SPEED = 709;
	public static final int FANG8_SET_COOLER7_SPEED = 710;
	public static final int FANG8_SET_COOLER8_SPEED = 711;
	public static final int ROADM_SET_CALIBRAR_FAIL_ON = 712;
	public static final int MUXDEMUXVOA_SET_CHANNELS_ATTENUATION = 760;
	public static final int MUXDEMUXVOA_SET_CH21_ATTENUATION = 761;
	public static final int MUXDEMUXVOA_SET_CH22_ATTENUATION = 762;
	public static final int MUXDEMUXVOA_SET_CH23_ATTENUATION = 763;
	public static final int MUXDEMUXVOA_SET_CH24_ATTENUATION = 764;
	public static final int MUXDEMUXVOA_SET_CH25_ATTENUATION = 765;
	public static final int MUXDEMUXVOA_SET_CH26_ATTENUATION = 766;
	public static final int MUXDEMUXVOA_SET_CH27_ATTENUATION = 767;
	public static final int MUXDEMUXVOA_SET_CH28_ATTENUATION = 768;
	public static final int MUXDEMUXVOA_SET_CH29_ATTENUATION = 769;
	public static final int MUXDEMUXVOA_SET_CH30_ATTENUATION = 770;
	public static final int MUXDEMUXVOA_SET_CH31_ATTENUATION = 771;
	public static final int MUXDEMUXVOA_SET_CH32_ATTENUATION = 772;
	public static final int MUXDEMUXVOA_SET_CH33_ATTENUATION = 773;
	public static final int MUXDEMUXVOA_SET_CH34_ATTENUATION = 774;
	public static final int MUXDEMUXVOA_SET_CH35_ATTENUATION = 775;
	public static final int MUXDEMUXVOA_SET_CH36_ATTENUATION = 776;
	public static final int MUXDEMUXVOA_SET_CH37_ATTENUATION = 777;
	public static final int MUXDEMUXVOA_SET_CH38_ATTENUATION = 778;
	public static final int MUXDEMUXVOA_SET_CH39_ATTENUATION = 779;
	public static final int MUXDEMUXVOA_SET_CH40_ATTENUATION = 780;
	public static final int MUXDEMUXVOA_SET_CH41_ATTENUATION = 781;
	public static final int MUXDEMUXVOA_SET_CH42_ATTENUATION = 782;
	public static final int MUXDEMUXVOA_SET_CH43_ATTENUATION = 783;
	public static final int MUXDEMUXVOA_SET_CH44_ATTENUATION = 784;
	public static final int MUXDEMUXVOA_SET_CH45_ATTENUATION = 785;
	public static final int MUXDEMUXVOA_SET_CH46_ATTENUATION = 786;
	public static final int MUXDEMUXVOA_SET_CH47_ATTENUATION = 787;
	public static final int MUXDEMUXVOA_SET_CH48_ATTENUATION = 788;
	public static final int MUXDEMUXVOA_SET_CH49_ATTENUATION = 789;
	public static final int MUXDEMUXVOA_SET_CH50_ATTENUATION = 790;
	public static final int MUXDEMUXVOA_SET_CH51_ATTENUATION = 791;
	public static final int MUXDEMUXVOA_SET_CH52_ATTENUATION = 792;
	public static final int MUXDEMUXVOA_SET_CH53_ATTENUATION = 793;
	public static final int MUXDEMUXVOA_SET_CH54_ATTENUATION = 794;
	public static final int MUXDEMUXVOA_SET_CH55_ATTENUATION = 795;
	public static final int MUXDEMUXVOA_SET_CH56_ATTENUATION = 796;
	public static final int MUXDEMUXVOA_SET_CH57_ATTENUATION = 797;
	public static final int MUXDEMUXVOA_SET_CH58_ATTENUATION = 798;
	public static final int MUXDEMUXVOA_SET_CH59_ATTENUATION = 799;
	public static final int MUXDEMUXVOA_SET_CH60_ATTENUATION = 800;
	public static final int AMP_ALS_ON = 801;
	public static final int AMP_ALS_OFF = 802;
	public static final int AMP_AUTORESTORE_ON = 803;
	public static final int AMP_AUTORESTORE_OFF = 804;
	public static final int AMP_MANUALRESTORE = 807;
	public static final int PPM_SET_LIMIARES_FAIL_LOW = 808;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_SOMA_CORRENTES = 809;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_1 = 810;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_2 = 811;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_3 = 812;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_4 = 813;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_5 = 814;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_6 = 815;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_7 = 816;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_8 = 817;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_9 = 818;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_10 = 819;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_11 = 820;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_12 = 821;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_SOMA_VIAS = 822;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_A = 823;
	public static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_B = 824;
	public static final int PPM_SET_LIMIARES_FAIL_HIGH = 825;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_SOMA_CORRENTES = 826;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_1 = 827;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_2 = 828;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_3 = 829;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_4 = 830;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_5 = 831;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_6 = 832;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_7 = 833;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_8 = 834;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_9 = 835;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_10 = 836;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_11 = 837;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_12 = 838;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_SOMA_VIAS = 839;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_A = 840;
	public static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_B = 841;
	public static final int SCMGR_SET_RESET_CONTADORES = 842;
	public static final int SUP_SET_BLOCKING_MANDATORY = 843;
	public static final int SUP_SET_REQUEST_OF_ACCESS = 844;
	public static final int TRP_SET_CLIENT_SD = 845;
	public static final int TRP_REG_AUTOLASEROFF_OFF = 846;
	public static final int TRP_REG_AUTOLASEROFF_ON = 847;
	public static final int FANG8_SET_FANS_ON = 848;
	public static final int FANG8_SET_FANS_OFF = 849;
	public static final int ODP_ENABLE = 850;
	public static final int ODP_DISABLE = 851;
	public static final int ODP_WAITTORESTORE_ENABLE = 852;
	public static final int ODP_WAITTORESTORE_DISABLE = 853;
	public static final int ODP_SET_WORKING_PATH = 854;
	public static final int ODP_SET_PROTECTION_PATH = 855;
	public static final int ODP_SET_WAITTORESTORE_TIME = 856;
	public static final int ODP_SET_HOLDOFF_TIME = 857;
	public static final int ODP_SIGNALDEGRADE_ENABLE = 858;
	public static final int ODP_SIGNALDEGRADE_DISABLE = 859;
	public static final int ODP_MANUAL_LASER_ON = 860;
	public static final int SUP_SET_NE_NAME = 861;
	public static final int SCMGRT_SET_ALS_ON = 862;
	public static final int SCMGRT_SET_ALS_OFF = 863;
	public static final int SCMGRT_SET_AUTORESTORE_ON = 864;
	public static final int SCMGRT_SET_AUTORESTORE_OFF = 865;
	public static final int SCMGRT_SET_MANUALRESTORE = 866;
	public static final int SCMGRC_SET_ALS_NORTH_ON = 867;
	public static final int SCMGRC_SET_ALS_NORTH_OFF = 868;
	public static final int SCMGRC_SET_AUTORESTORE_NORTH_ON = 869;
	public static final int SCMGRC_SET_AUTORESTORE_NORTH_OFF = 870;
	public static final int SCMGRC_SET_MANUALRESTORE_NORTH = 871;
	public static final int SCMGRC_SET_ALS_SOUTH_ON = 872;
	public static final int SCMGRC_SET_ALS_SOUTH_OFF = 873;
	public static final int SCMGRC_SET_AUTORESTORE_SOUTH_ON = 874;
	public static final int SCMGRC_SET_AUTORESTORE_SOUTH_OFF = 875;
	public static final int SCMGRC_SET_MANUALRESTORE_SOUTH = 876;
	private static final int TRP_SET_LASER_ON_CODE = 1;
	private static final int TRP_SET_LASER_OFF_CODE = 2;
	private static final int TRP_SET_LASER2_ON_CODE = 3;
	private static final int TRP_SET_LASER2_OFF_CODE = 4;
	private static final int TRP_RESET_COUNT_CODE = 160;
	private static final int TRP_ODU_SAPI_TX_CODE = -79;
	private static final int TRP_ODU_DAPI_TX_CODE = -78;
	private static final int TRP_ODU_SAPI_RX_CODE = -77;
	private static final int TRP_ODU_DAPI_RX_CODE = -76;
	private static final int TRP_OTU_SAPI_TX_CODE = -70;
	private static final int TRP_OTU_DAPI_TX_CODE = -69;
	private static final int TRP_OTU_SAPI_RX_CODE = -68;
	private static final int TRP_OTU_DAPI_RX_CODE = -67;
	private static final int TRP_OTU_SAPI2_TX_CODE = -55;
	private static final int TRP_OTU_DAPI2_TX_CODE = -54;
	private static final int TRP_OTU_SAPI2_RX_CODE = -53;
	private static final int TRP_OTU_DAPI2_RX_CODE = -52;
	private static final int TRP_FEC_TX_OFF_CODE = -91;
	private static final int TRP_FEC_TX_ON_CODE = -90;
	private static final int TRP_FEC_RX_OFF_CODE = -89;
	private static final int TRP_FEC_RX_ON_CODE = -88;
	private static final int TRP_FEC2_TX_OFF_CODE = -59;
	private static final int TRP_FEC2_TX_ON_CODE = -58;
	private static final int TRP_FEC2_RX_OFF_CODE = -57;
	private static final int TRP_FEC2_RX_ON_CODE = -56;
	private static final int TRP_TIM_ACT_ON_CODE = -73;
	private static final int TRP_TIM_ACT_OFF_CODE = -72;
	private static final int TRP_AUTOLASEROFF_OFF_CODE = -86;
	private static final int TRP_AUTOLASEROFF_ON_CODE = -87;
	private static final int TRP_AUTOLASEROFF2_OFF_CODE = -84;
	private static final int TRP_AUTOLASEROFF2_ON_CODE = -85;
	private static final int TRP_SET_TAXA_MODE_CODE = -61;
	private static final int TRP_SET_MAX_FRAME_SIZE_CODE = 244;
	private static final int TRP_SET_J0_RX_CODE = 246;
	private static final int TRP_SET_J0_TX_CODE = 248;
	private static final int TRP_SET_TIM_ACT_RS_ON_CODE = 173;
	private static final int TRP_SET_TIM_ACT_RS_OFF_CODE = 174;
	private static final int TRP_SET_MODO_J0_TERMINADO_ON_CODE = 175;
	private static final int TRP_SET_MODO_J0_TERMINADO_OFF_CODE = 176;
	private static final int SUP_CONT_RESET_CODE = 21;
	private static final int SUP_RACK_CONF_CODE = 23;
//	private static final int SUP_RACK_CONF_SPVL_CODE = 38;
	public static final int SUP_RACK_CONF_SPVL_CODE = 38;
	private static final int FAN_SET_MAX_TEMP_CODE = 2;
	private static final int FAN_SET_VELOCITY_CODE = 4;
	private static final int TRP_SET_TAXA_CODE = 33;
	private static final int AMP_SET_LASER_ON_CODE = 1;
	private static final int AMP_SET_LASER_OFF_CODE = 2;
	private static final int AMP_SET_AGC_GAIN_CODE = 58;
	private static final int OPS_SWITCH_CODE = 6;
	private static final int OPS_LOCK_CODE = 7;
	private static final int OPS_AUTO_CODE = 8;
	private static final int SW8_AUTO_MANUAL_CODE = 2;
	private static final int SW8_SET_CHANNEL_CODE = 4;
	private static final int SW8_SWITCH_CODE = 6;
	private static final int SUP_BOOT_CODE = 3;
	private static final int SUP_ADDR_CODE = 4;
	private static final int SUP_NET_CODE = 5;
	private static final int SUP_CLOCK_CODE = 7;
	private static final int SUP_LOCK_CODE = 18;
	private static final int SUP_UNBLOCK_CODE = 19;
	private static final int SUP_CONN_CLOSE_CODE = 20;
	private static final int SUP_HISTORY_CODE = 21;
	public static final int SUP_CONF_CODE = 22;
	public static final int SUP_HARD_BOOT_CODE = 25;
	public static final int SUP_SET_SEGMENTATION_CODE = 32794;
	public static final int MUX_SFP1_ONOFF_CODE = 227;
	public static final int MUX_SFP2_ONOFF_CODE = 228;
	public static final int MUX_SFP3_ONOFF_CODE = 229;
	public static final int MUX_SFP4_ONOFF_CODE = 230;
	public static final int COMB_SFP_ON_CODE = 3;
	public static final int COMB_SFP_OFF_CODE = 4;
	public static final int COMB_CLIENT_PORT_CONF_CODE = 5;
	public static final int COMB_AUTOSENSE_ON_CODE = 76;
	public static final int COMB_AUTOSENSE_OFF_CODE = 77;
	public static final int COMB_AUTOLASEROFF_ON_CODE = 171;
	public static final int COMB_AUTOLASEROFF_OFF_CODE = 172;
	public static final int COMB_FPGA_RESET_CODE = 252;
	public static final int AMP_RESTART_CODE = 3;
	public static final int AMP_TURN_ON_EYE_PROTECTION_CODE = 251;
	public static final int AMP_TURN_ON_APR_CODE = 218;
	public static final int AMP_TURN_ON_AGC_CODE = 253;
	public static final int AMP_EXPORT_CONFIG_CODE = 217;
	public static final int AMP_TURN_LASER_ON_OFF_CODE = 252;
	public static final int AMP_SAVE_CONFIG_LIMIAR_POTOUT_CODE = 242;
	public static final int AMP_SAVE_CONFIG_LIMIAR_POTIN_CODE = 243;
	public static final int AMP_SAVE_CONFIG_LIMIAR_CORR_CODE = 244;
	public static final int AMP_SAVE_CONFIG_LIMIAR_POT_CODE = 245;
	public static final int AMP_SAVE_CONFIG_GANHO_AGC_CODE = 246;
	public static final int AMP_SAVE_CONFIG_POTL1_CODE = 250;
	public static final int AMP_SAVE_CONFIG_POTL2_CODE = 249;
	public static final int AMP_SAVE_CONFIG_POTL3_CODE = 248;
	public static final int AMP_SAVE_CONFIG_POTL4_CODE = 247;
	public static final int AMP_SAVE_IP_CODE = 2;
	public static final int AMP_TRAP_IP_CODE = 7;
	public static final int AMP_TRAP_PORTA_CODE = 8;
	public static final int AMP_TRAP_RW_COMMUNITY_CODE = 215;
	public static final int AMP_TRAP_RO_COMMUNITY_CODE = 216;
	public static final int AMP_IP_APR_CODE = 6;
	public static final int SUP_HISRESTART_CODE = 24;
	private static final int ROADM_SET_CHANNEL_CONFIG_ALL_CODE = 58624;
	private static final int ROADM_SET_CHANNEL_CONFIG_00_CODE = 58625;
	private static final int ROADM_SET_CHANNEL_CONFIG_01_CODE = 58626;
	private static final int ROADM_SET_CHANNEL_CONFIG_02_CODE = 58627;
	private static final int ROADM_SET_CHANNEL_CONFIG_03_CODE = 58628;
	private static final int ROADM_SET_CHANNEL_CONFIG_04_CODE = 58629;
	private static final int ROADM_SET_CHANNEL_CONFIG_05_CODE = 58630;
	private static final int ROADM_SET_CHANNEL_CONFIG_06_CODE = 58631;
	private static final int ROADM_SET_CHANNEL_CONFIG_07_CODE = 58632;
	private static final int ROADM_SET_CHANNEL_CONFIG_08_CODE = 58633;
	private static final int ROADM_SET_CHANNEL_CONFIG_09_CODE = 58634;
	private static final int ROADM_SET_CHANNEL_CONFIG_10_CODE = 58635;
	private static final int ROADM_SET_CHANNEL_CONFIG_11_CODE = 58636;
	private static final int ROADM_SET_CHANNEL_CONFIG_12_CODE = 58637;
	private static final int ROADM_SET_CHANNEL_CONFIG_13_CODE = 58638;
	private static final int ROADM_SET_CHANNEL_CONFIG_14_CODE = 58639;
	private static final int ROADM_SET_CHANNEL_CONFIG_15_CODE = 58640;
	private static final int ROADM_SET_CHANNEL_CONFIG_16_CODE = 58641;
	private static final int ROADM_SET_CHANNEL_CONFIG_17_CODE = 58642;
	private static final int ROADM_SET_CHANNEL_CONFIG_18_CODE = 58643;
	private static final int ROADM_SET_CHANNEL_CONFIG_19_CODE = 58644;
	private static final int ROADM_SET_CHANNEL_CONFIG_20_CODE = 58645;
	private static final int ROADM_SET_CHANNEL_CONFIG_21_CODE = 58646;
	private static final int ROADM_SET_CHANNEL_CONFIG_22_CODE = 58647;
	private static final int ROADM_SET_CHANNEL_CONFIG_23_CODE = 58648;
	private static final int ROADM_SET_CHANNEL_CONFIG_24_CODE = 58649;
	private static final int ROADM_SET_CHANNEL_CONFIG_25_CODE = 58650;
	private static final int ROADM_SET_CHANNEL_CONFIG_26_CODE = 58651;
	private static final int ROADM_SET_CHANNEL_CONFIG_27_CODE = 58652;
	private static final int ROADM_SET_CHANNEL_CONFIG_28_CODE = 58653;
	private static final int ROADM_SET_CHANNEL_CONFIG_29_CODE = 58654;
	private static final int ROADM_SET_CHANNEL_CONFIG_30_CODE = 58655;
	private static final int ROADM_SET_CHANNEL_CONFIG_31_CODE = 58656;
	private static final int ROADM_SET_CHANNEL_CONFIG_32_CODE = 58657;
	private static final int ROADM_SET_CHANNEL_CONFIG_33_CODE = 58658;
	private static final int ROADM_SET_CHANNEL_CONFIG_34_CODE = 58659;
	private static final int ROADM_SET_CHANNEL_CONFIG_35_CODE = 58660;
	private static final int ROADM_SET_CHANNEL_CONFIG_36_CODE = 58661;
	private static final int ROADM_SET_CHANNEL_CONFIG_37_CODE = 58662;
	private static final int ROADM_SET_CHANNEL_CONFIG_38_CODE = 58663;
	private static final int ROADM_SET_CHANNEL_CONFIG_39_CODE = 58664;
	private static final int ROADM_SET_VOA_ATT_DEMUX_CODE = 57728;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_00_CODE = 57473;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_01_CODE = 57474;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_02_CODE = 57475;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_03_CODE = 57476;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_04_CODE = 57477;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_05_CODE = 57478;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_06_CODE = 57479;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_07_CODE = 57480;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_08_CODE = 57481;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_09_CODE = 57482;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_10_CODE = 57483;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_11_CODE = 57484;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_12_CODE = 57485;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_13_CODE = 57486;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_14_CODE = 57487;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_15_CODE = 57488;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_16_CODE = 57489;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_17_CODE = 57490;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_18_CODE = 57491;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_19_CODE = 57492;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_20_CODE = 57493;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_21_CODE = 57494;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_22_CODE = 57495;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_23_CODE = 57496;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_24_CODE = 57497;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_25_CODE = 57498;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_26_CODE = 57499;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_27_CODE = 57500;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_28_CODE = 57501;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_29_CODE = 57502;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_30_CODE = 57503;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_31_CODE = 57504;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_32_CODE = 57505;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_33_CODE = 57506;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_34_CODE = 57507;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_35_CODE = 57508;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_36_CODE = 57509;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_37_CODE = 57510;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_38_CODE = 57511;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_39_CODE = 57512;
	private static final int ROADM_SET_VOA_ATT_ADD_00_CODE = 57601;
	private static final int ROADM_SET_VOA_ATT_ADD_01_CODE = 57602;
	private static final int ROADM_SET_VOA_ATT_ADD_02_CODE = 57603;
	private static final int ROADM_SET_VOA_ATT_ADD_03_CODE = 57604;
	private static final int ROADM_SET_VOA_ATT_ADD_04_CODE = 57605;
	private static final int ROADM_SET_VOA_ATT_ADD_05_CODE = 57606;
	private static final int ROADM_SET_VOA_ATT_ADD_06_CODE = 57607;
	private static final int ROADM_SET_VOA_ATT_ADD_07_CODE = 57608;
	private static final int ROADM_SET_VOA_ATT_ADD_08_CODE = 57609;
	private static final int ROADM_SET_VOA_ATT_ADD_09_CODE = 57610;
	private static final int ROADM_SET_VOA_ATT_ADD_10_CODE = 57611;
	private static final int ROADM_SET_VOA_ATT_ADD_11_CODE = 57612;
	private static final int ROADM_SET_VOA_ATT_ADD_12_CODE = 57613;
	private static final int ROADM_SET_VOA_ATT_ADD_13_CODE = 57614;
	private static final int ROADM_SET_VOA_ATT_ADD_14_CODE = 57615;
	private static final int ROADM_SET_VOA_ATT_ADD_15_CODE = 57616;
	private static final int ROADM_SET_VOA_ATT_ADD_16_CODE = 57617;
	private static final int ROADM_SET_VOA_ATT_ADD_17_CODE = 57618;
	private static final int ROADM_SET_VOA_ATT_ADD_18_CODE = 57619;
	private static final int ROADM_SET_VOA_ATT_ADD_19_CODE = 57620;
	private static final int ROADM_SET_VOA_ATT_ADD_20_CODE = 57621;
	private static final int ROADM_SET_VOA_ATT_ADD_21_CODE = 57622;
	private static final int ROADM_SET_VOA_ATT_ADD_22_CODE = 57623;
	private static final int ROADM_SET_VOA_ATT_ADD_23_CODE = 57624;
	private static final int ROADM_SET_VOA_ATT_ADD_24_CODE = 57625;
	private static final int ROADM_SET_VOA_ATT_ADD_25_CODE = 57626;
	private static final int ROADM_SET_VOA_ATT_ADD_26_CODE = 57627;
	private static final int ROADM_SET_VOA_ATT_ADD_27_CODE = 57628;
	private static final int ROADM_SET_VOA_ATT_ADD_28_CODE = 57629;
	private static final int ROADM_SET_VOA_ATT_ADD_29_CODE = 57630;
	private static final int ROADM_SET_VOA_ATT_ADD_30_CODE = 57631;
	private static final int ROADM_SET_VOA_ATT_ADD_31_CODE = 57632;
	private static final int ROADM_SET_VOA_ATT_ADD_32_CODE = 57633;
	private static final int ROADM_SET_VOA_ATT_ADD_33_CODE = 57634;
	private static final int ROADM_SET_VOA_ATT_ADD_34_CODE = 57635;
	private static final int ROADM_SET_VOA_ATT_ADD_35_CODE = 57636;
	private static final int ROADM_SET_VOA_ATT_ADD_36_CODE = 57637;
	private static final int ROADM_SET_VOA_ATT_ADD_37_CODE = 57638;
	private static final int ROADM_SET_VOA_ATT_ADD_38_CODE = 57639;
	private static final int ROADM_SET_VOA_ATT_ADD_39_CODE = 57640;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_ALL_CHANNELS_CODE = 58752;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_00_CODE = 58753;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_01_CODE = 58754;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_02_CODE = 58755;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_03_CODE = 58756;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_04_CODE = 58757;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_05_CODE = 58758;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_06_CODE = 58759;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_07_CODE = 58760;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_08_CODE = 58761;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_09_CODE = 58762;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_10_CODE = 58763;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_11_CODE = 58764;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_12_CODE = 58765;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_13_CODE = 58766;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_14_CODE = 58767;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_15_CODE = 58768;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_16_CODE = 58769;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_17_CODE = 58770;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_18_CODE = 58771;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_19_CODE = 58772;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_20_CODE = 58773;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_21_CODE = 58774;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_22_CODE = 58775;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_23_CODE = 58776;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_24_CODE = 58777;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_25_CODE = 58778;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_26_CODE = 58779;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_27_CODE = 58780;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_28_CODE = 58781;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_29_CODE = 58782;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_30_CODE = 58783;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_31_CODE = 58784;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_32_CODE = 58785;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_33_CODE = 58786;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_34_CODE = 58787;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_35_CODE = 58788;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_36_CODE = 58789;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_37_CODE = 58790;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_38_CODE = 58791;
	private static final int ROADM_SET_AUTO_SWITCHING_ON_39_CODE = 58792;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_ALL_CHANNELS_CODE = 58880;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_00_CODE = 58881;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_01_CODE = 58882;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_02_CODE = 58883;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_03_CODE = 58884;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_04_CODE = 58885;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_05_CODE = 58886;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_06_CODE = 58887;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_07_CODE = 58888;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_08_CODE = 58889;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_09_CODE = 58890;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_10_CODE = 58891;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_11_CODE = 58892;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_12_CODE = 58893;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_13_CODE = 58894;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_14_CODE = 58895;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_15_CODE = 58896;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_16_CODE = 58897;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_17_CODE = 58898;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_18_CODE = 58899;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_19_CODE = 58900;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_20_CODE = 58901;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_21_CODE = 58902;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_22_CODE = 58903;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_23_CODE = 58904;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_24_CODE = 58905;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_25_CODE = 58906;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_26_CODE = 58907;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_27_CODE = 58908;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_28_CODE = 58909;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_29_CODE = 58910;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_30_CODE = 58911;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_31_CODE = 58912;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_32_CODE = 58913;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_33_CODE = 58914;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_34_CODE = 58915;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_35_CODE = 58916;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_36_CODE = 58917;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_37_CODE = 58918;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_38_CODE = 58919;
	private static final int ROADM_SET_AUTO_SWITCHING_OFF_39_CODE = 58920;
	private static final int ROADM_SET_MANUAL_SWITCHING_ALL_CHANNELS_CODE = 58496;
	private static final int ROADM_SET_MANUAL_SWITCHING_00_CODE = 58497;
	private static final int ROADM_SET_MANUAL_SWITCHING_01_CODE = 58498;
	private static final int ROADM_SET_MANUAL_SWITCHING_02_CODE = 58499;
	private static final int ROADM_SET_MANUAL_SWITCHING_03_CODE = 58500;
	private static final int ROADM_SET_MANUAL_SWITCHING_04_CODE = 58501;
	private static final int ROADM_SET_MANUAL_SWITCHING_05_CODE = 58502;
	private static final int ROADM_SET_MANUAL_SWITCHING_06_CODE = 58503;
	private static final int ROADM_SET_MANUAL_SWITCHING_07_CODE = 58504;
	private static final int ROADM_SET_MANUAL_SWITCHING_08_CODE = 58505;
	private static final int ROADM_SET_MANUAL_SWITCHING_09_CODE = 58506;
	private static final int ROADM_SET_MANUAL_SWITCHING_10_CODE = 58507;
	private static final int ROADM_SET_MANUAL_SWITCHING_11_CODE = 58508;
	private static final int ROADM_SET_MANUAL_SWITCHING_12_CODE = 58509;
	private static final int ROADM_SET_MANUAL_SWITCHING_13_CODE = 58510;
	private static final int ROADM_SET_MANUAL_SWITCHING_14_CODE = 58511;
	private static final int ROADM_SET_MANUAL_SWITCHING_15_CODE = 58512;
	private static final int ROADM_SET_MANUAL_SWITCHING_16_CODE = 58513;
	private static final int ROADM_SET_MANUAL_SWITCHING_17_CODE = 58514;
	private static final int ROADM_SET_MANUAL_SWITCHING_18_CODE = 58515;
	private static final int ROADM_SET_MANUAL_SWITCHING_19_CODE = 58516;
	private static final int ROADM_SET_MANUAL_SWITCHING_20_CODE = 58517;
	private static final int ROADM_SET_MANUAL_SWITCHING_21_CODE = 58518;
	private static final int ROADM_SET_MANUAL_SWITCHING_22_CODE = 58519;
	private static final int ROADM_SET_MANUAL_SWITCHING_23_CODE = 58520;
	private static final int ROADM_SET_MANUAL_SWITCHING_24_CODE = 58521;
	private static final int ROADM_SET_MANUAL_SWITCHING_25_CODE = 58522;
	private static final int ROADM_SET_MANUAL_SWITCHING_26_CODE = 58523;
	private static final int ROADM_SET_MANUAL_SWITCHING_27_CODE = 58524;
	private static final int ROADM_SET_MANUAL_SWITCHING_28_CODE = 58525;
	private static final int ROADM_SET_MANUAL_SWITCHING_29_CODE = 58526;
	private static final int ROADM_SET_MANUAL_SWITCHING_30_CODE = 58527;
	private static final int ROADM_SET_MANUAL_SWITCHING_31_CODE = 58528;
	private static final int ROADM_SET_MANUAL_SWITCHING_32_CODE = 58529;
	private static final int ROADM_SET_MANUAL_SWITCHING_33_CODE = 58530;
	private static final int ROADM_SET_MANUAL_SWITCHING_34_CODE = 58531;
	private static final int ROADM_SET_MANUAL_SWITCHING_35_CODE = 58532;
	private static final int ROADM_SET_MANUAL_SWITCHING_36_CODE = 58533;
	private static final int ROADM_SET_MANUAL_SWITCHING_37_CODE = 58534;
	private static final int ROADM_SET_MANUAL_SWITCHING_38_CODE = 58535;
	private static final int ROADM_SET_MANUAL_SWITCHING_39_CODE = 58536;
	private static final int ROADM_SET_POT_ALL_CHANNELS_CODE = 57344;
	private static final int ROADM_SET_POT_00_CODE = 57345;
	private static final int ROADM_SET_POT_01_CODE = 57346;
	private static final int ROADM_SET_POT_02_CODE = 57347;
	private static final int ROADM_SET_POT_03_CODE = 57348;
	private static final int ROADM_SET_POT_04_CODE = 57349;
	private static final int ROADM_SET_POT_05_CODE = 57350;
	private static final int ROADM_SET_POT_06_CODE = 57351;
	private static final int ROADM_SET_POT_07_CODE = 57352;
	private static final int ROADM_SET_POT_08_CODE = 57353;
	private static final int ROADM_SET_POT_09_CODE = 57354;
	private static final int ROADM_SET_POT_10_CODE = 57355;
	private static final int ROADM_SET_POT_11_CODE = 57356;
	private static final int ROADM_SET_POT_12_CODE = 57357;
	private static final int ROADM_SET_POT_13_CODE = 57358;
	private static final int ROADM_SET_POT_14_CODE = 57359;
	private static final int ROADM_SET_POT_15_CODE = 57360;
	private static final int ROADM_SET_POT_16_CODE = 57361;
	private static final int ROADM_SET_POT_17_CODE = 57362;
	private static final int ROADM_SET_POT_18_CODE = 57363;
	private static final int ROADM_SET_POT_19_CODE = 57364;
	private static final int ROADM_SET_POT_20_CODE = 57365;
	private static final int ROADM_SET_POT_21_CODE = 57366;
	private static final int ROADM_SET_POT_22_CODE = 57367;
	private static final int ROADM_SET_POT_23_CODE = 57368;
	private static final int ROADM_SET_POT_24_CODE = 57369;
	private static final int ROADM_SET_POT_25_CODE = 57370;
	private static final int ROADM_SET_POT_26_CODE = 57371;
	private static final int ROADM_SET_POT_27_CODE = 57372;
	private static final int ROADM_SET_POT_28_CODE = 57373;
	private static final int ROADM_SET_POT_29_CODE = 57374;
	private static final int ROADM_SET_POT_30_CODE = 57375;
	private static final int ROADM_SET_POT_31_CODE = 57376;
	private static final int ROADM_SET_POT_32_CODE = 57377;
	private static final int ROADM_SET_POT_33_CODE = 57378;
	private static final int ROADM_SET_POT_34_CODE = 57379;
	private static final int ROADM_SET_POT_35_CODE = 57380;
	private static final int ROADM_SET_POT_36_CODE = 57381;
	private static final int ROADM_SET_POT_37_CODE = 57382;
	private static final int ROADM_SET_POT_38_CODE = 57383;
	private static final int ROADM_SET_POT_39_CODE = 57384;
	private static final int CLOSED_LOOP_MODE_ON_CODE = 59136;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_1_CODE = 59137;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_2_CODE = 59138;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_3_CODE = 59139;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_4_CODE = 59140;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_5_CODE = 59141;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_6_CODE = 59142;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_7_CODE = 59143;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_8_CODE = 59144;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_9_CODE = 59145;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_10_CODE = 59146;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_11_CODE = 59147;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_12_CODE = 59148;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_13_CODE = 59149;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_14_CODE = 59150;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_15_CODE = 59151;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_16_CODE = 59152;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_17_CODE = 59153;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_18_CODE = 59154;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_19_CODE = 59155;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_20_CODE = 59156;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_21_CODE = 59157;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_22_CODE = 59158;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_23_CODE = 59159;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_24_CODE = 59160;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_25_CODE = 59161;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_26_CODE = 59162;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_27_CODE = 59163;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_28_CODE = 59164;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_29_CODE = 59165;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_30_CODE = 59166;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_31_CODE = 59167;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_32_CODE = 59168;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_33_CODE = 59169;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_34_CODE = 59170;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_35_CODE = 59171;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_36_CODE = 59172;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_37_CODE = 59173;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_38_CODE = 59174;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_39_CODE = 59175;
	private static final int CLOSED_LOOP_MODE_ON_CHANNEL_40_CODE = 59176;
	private static final int CLOSED_LOOP_MODE_OFF_CODE = 59264;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_1_CODE = 59265;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_2_CODE = 59266;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_3_CODE = 59267;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_4_CODE = 59268;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_5_CODE = 59269;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_6_CODE = 59270;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_7_CODE = 59271;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_8_CODE = 59272;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_9_CODE = 59273;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_10_CODE = 59274;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_11_CODE = 59275;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_12_CODE = 59276;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_13_CODE = 59277;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_14_CODE = 59278;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_15_CODE = 59279;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_16_CODE = 59280;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_17_CODE = 59281;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_18_CODE = 59282;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_19_CODE = 59283;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_20_CODE = 59284;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_21_CODE = 59285;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_22_CODE = 59286;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_23_CODE = 59287;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_24_CODE = 59288;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_25_CODE = 59289;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_26_CODE = 59290;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_27_CODE = 59291;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_28_CODE = 59292;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_29_CODE = 59293;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_30_CODE = 59294;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_31_CODE = 59295;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_32_CODE = 59296;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_33_CODE = 59297;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_34_CODE = 59298;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_35_CODE = 59299;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_36_CODE = 59300;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_37_CODE = 59301;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_38_CODE = 59302;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_39_CODE = 59303;
	private static final int CLOSED_LOOP_MODE_OFF_CHANNEL_40_CODE = 59304;
	private static final int ROAM_SET_CALIBRATE_CODE = 59392;
	private static final int ROADM_SET_DEMUX_CALIBRATION_RESTART_CODE = 59520;
	private static final int ROADM_SET_VOA_ATT_ADD_ALL_CODE = 57600;
	private static final int ROADM_SET_VOA_ATT_EXPRESS_ALL_CODE = 57472;
	private static final int ROADM_SET_NEXT_ROADM_BOARD_CODE = 57856;
	private static final int ROADM_SET_GROUP_CODE = 57984;
	private static final int ROADM_SET_SIDE_CODE = 58112;
	private static final int ROADM_SET_LOOP_STATE_CODE = 58240;
	private static final int ROADM_SET_ATENUATION_THRESHOLD_CODE = 58368;
	private static final int ROADM_SET_CALIBRAR_FAIL_ON_CODE = 59008;
	private static final int TRP_ITU_CHANNEL_LADO1_CODE = 15;
	private static final int TRP_ITU_CHANNEL_LADO2_CODE = 47;
	private static final int AMP_AGC_ON_CODE = 59;
	private static final int AMP_AGC_OFF_CODE = 60;
	private static final int AMP_EYE_ON_CODE = 3;
	private static final int AMP_EYE_OFF_CODE = 4;
	private static final int FANG8_SET_SENSORS_TEMP_THR_CODE = 33024;
	private static final int FANG8_SET_SENSOR1_TEMP_THR_CODE = 33025;
	private static final int FANG8_SET_SENSOR2_TEMP_THR_CODE = 33026;
	private static final int FANG8_SET_COOLERS_SPEED_CODE = 33280;
	private static final int FANG8_SET_COOLER1_SPEED_CODE = 33281;
	private static final int FANG8_SET_COOLER2_SPEED_CODE = 33282;
	private static final int FANG8_SET_COOLER3_SPEED_CODE = 33283;
	private static final int FANG8_SET_COOLER4_SPEED_CODE = 33284;
	private static final int FANG8_SET_COOLER5_SPEED_CODE = 33285;
	private static final int FANG8_SET_COOLER6_SPEED_CODE = 33286;
	private static final int FANG8_SET_COOLER7_SPEED_CODE = 33287;
	private static final int FANG8_SET_COOLER8_SPEED_CODE = 33288;
	private static final int FANG8_SET_FANS_OFF_CODE = 34048;
	private static final int FANG8_SET_FANS_ON_CODE = 34304;
	public static final int MUXDEMUXVOA_SET_CHANNELS_ATTENUATION_CODE = 57344;
	public static final int MUXDEMUXVOA_SET_CH21_ATTENUATION_CODE = 57345;
	public static final int MUXDEMUXVOA_SET_CH22_ATTENUATION_CODE = 57346;
	public static final int MUXDEMUXVOA_SET_CH23_ATTENUATION_CODE = 57347;
	public static final int MUXDEMUXVOA_SET_CH24_ATTENUATION_CODE = 57348;
	public static final int MUXDEMUXVOA_SET_CH25_ATTENUATION_CODE = 57349;
	public static final int MUXDEMUXVOA_SET_CH26_ATTENUATION_CODE = 57350;
	public static final int MUXDEMUXVOA_SET_CH27_ATTENUATION_CODE = 57351;
	public static final int MUXDEMUXVOA_SET_CH28_ATTENUATION_CODE = 57352;
	public static final int MUXDEMUXVOA_SET_CH29_ATTENUATION_CODE = 57353;
	public static final int MUXDEMUXVOA_SET_CH30_ATTENUATION_CODE = 57354;
	public static final int MUXDEMUXVOA_SET_CH31_ATTENUATION_CODE = 57355;
	public static final int MUXDEMUXVOA_SET_CH32_ATTENUATION_CODE = 57356;
	public static final int MUXDEMUXVOA_SET_CH33_ATTENUATION_CODE = 57357;
	public static final int MUXDEMUXVOA_SET_CH34_ATTENUATION_CODE = 57358;
	public static final int MUXDEMUXVOA_SET_CH35_ATTENUATION_CODE = 57359;
	public static final int MUXDEMUXVOA_SET_CH36_ATTENUATION_CODE = 57360;
	public static final int MUXDEMUXVOA_SET_CH37_ATTENUATION_CODE = 57361;
	public static final int MUXDEMUXVOA_SET_CH38_ATTENUATION_CODE = 57362;
	public static final int MUXDEMUXVOA_SET_CH39_ATTENUATION_CODE = 57363;
	public static final int MUXDEMUXVOA_SET_CH40_ATTENUATION_CODE = 57364;
	public static final int MUXDEMUXVOA_SET_CH41_ATTENUATION_CODE = 57365;
	public static final int MUXDEMUXVOA_SET_CH42_ATTENUATION_CODE = 57366;
	public static final int MUXDEMUXVOA_SET_CH43_ATTENUATION_CODE = 57367;
	public static final int MUXDEMUXVOA_SET_CH44_ATTENUATION_CODE = 57368;
	public static final int MUXDEMUXVOA_SET_CH45_ATTENUATION_CODE = 57369;
	public static final int MUXDEMUXVOA_SET_CH46_ATTENUATION_CODE = 57370;
	public static final int MUXDEMUXVOA_SET_CH47_ATTENUATION_CODE = 57371;
	public static final int MUXDEMUXVOA_SET_CH48_ATTENUATION_CODE = 57372;
	public static final int MUXDEMUXVOA_SET_CH49_ATTENUATION_CODE = 57373;
	public static final int MUXDEMUXVOA_SET_CH50_ATTENUATION_CODE = 57374;
	public static final int MUXDEMUXVOA_SET_CH51_ATTENUATION_CODE = 57375;
	public static final int MUXDEMUXVOA_SET_CH52_ATTENUATION_CODE = 57376;
	public static final int MUXDEMUXVOA_SET_CH53_ATTENUATION_CODE = 57377;
	public static final int MUXDEMUXVOA_SET_CH54_ATTENUATION_CODE = 57378;
	public static final int MUXDEMUXVOA_SET_CH55_ATTENUATION_CODE = 57379;
	public static final int MUXDEMUXVOA_SET_CH56_ATTENUATION_CODE = 57380;
	public static final int MUXDEMUXVOA_SET_CH57_ATTENUATION_CODE = 57381;
	public static final int MUXDEMUXVOA_SET_CH58_ATTENUATION_CODE = 57382;
	public static final int MUXDEMUXVOA_SET_CH59_ATTENUATION_CODE = 57383;
	public static final int MUXDEMUXVOA_SET_CH60_ATTENUATION_CODE = 57384;
	public static final int SCMGR_SET_RESET_CONTADORES_CODE = 41216;
	public static final int SCMGR_SET_ALS_ON_CODE = 57857;
	public static final int SCMGR_SET_ALS_OFF_CODE = 57905;
	public static final int SCMGR_SET_AUTORESTORE_ON_CODE = 57873;
	public static final int SCMGR_SET_AUTORESTORE_OFF_CODE = 57921;
	public static final int SCMGR_SET_MANUALRESTORE_CODE = 57889;
	public static final int SCMGRC_SET_ALS_NORTH_ON_CODE = 57858;
	public static final int SCMGRC_SET_ALS_NORTH_OFF_CODE = 57906;
	public static final int SCMGRC_SET_AUTORESTORE_NORTH_ON_CODE = 57874;
	public static final int SCMGRC_SET_AUTORESTORE_NORTH_OFF_CODE = 57922;
	public static final int SCMGRC_SET_MANUALRESTORE_NORTH_CODE = 57890;
	public static final int AMP_ALS_ON_CODE = 64;
	public static final int AMP_ALS_OFF_CODE = 65;
	public static final int AMP_AUTORESTORE_ON_CODE = 66;
	public static final int AMP_AUTORESTORE_OFF_CODE = 67;
	public static final int AMP_BOOSTERPRE_ON_CODE = 68;
	public static final int AMP_BOOSTERPRE_OFF_CODE = 69;
	public static final int AMP_MANUALRESTORE_CODE = 70;
	private static final int PPM_SET_LIMIARES_FAIL_LOW_CODE = 57600;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_SOMA_CORRENTES_CODE = 57601;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_1_CODE = 57602;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_2_CODE = 57603;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_3_CODE = 57604;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_4_CODE = 57605;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_5_CODE = 57606;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_6_CODE = 57607;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_7_CODE = 57608;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_8_CODE = 57609;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_9_CODE = 921754;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_10_CODE = 57611;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_11_CODE = 57612;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_12_CODE = 57613;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_SOMA_VIAS_CODE = 57614;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_A_CODE = 57615;
	private static final int PPM_SET_LIMIAR_FAIL_LOW_VIA_B_CODE = 57616;
	private static final int PPM_SET_LIMIARES_FAIL_HIGH_CODE = 57856;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_SOMA_CORRENTES_CODE = 57857;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_1_CODE = 57858;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_2_CODE = 57859;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_3_CODE = 57860;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_4_CODE = 57861;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_5_CODE = 57862;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_6_CODE = 57863;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_7_CODE = 57864;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_8_CODE = 57865;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_9_CODE = 57866;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_10_CODE = 57867;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_11_CODE = 57868;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_12_CODE = 57869;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_SOMA_VIAS_CODE = 57870;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_A_CODE = 57871;
	private static final int PPM_SET_LIMIAR_FAIL_HIGH_VIA_B_CODE = 57872;
	public static final int ODP_ENABLE_CODE = 144;
	public static final int ODP_DISABLE_CODE = 145;
	public static final int ODP_SET_WORKING_PATH_CODE = 146;
	public static final int ODP_SET_PROTECTION_PATH_CODE = 147;
	public static final int ODP_SIGNALDEGRADE_ENABLE_CODE = 148;
	public static final int ODP_SIGNALDEGRADE_DISABLE_CODE = 149;
	public static final int ODP_WAITTORESTORE_ENABLE_CODE = 150;
	public static final int ODP_WAITTORESTORE_DISABLE_CODE = 151;
	public static final int ODP_SET_WAITTORESTORE_TIME_CODE = 152;
	public static final int ODP_SET_HOLDOFF_TIME_CODE = 154;
	public static final int ODP_MANUAL_LASER_ON_CODE = 159;
	public static final int SUP_SET_BLOCKING_MANDATORY_CODE = 27;
	public static final int SUP_SET_REQUEST_OF_ACCESS_CODE = 28;
	public static final int TRP_SET_CLIENT_SD_CODE = 250;
	public static final int SUP_SET_NE_NAME_CODE = 32;
	private static final int INVALID_CODE = -2;
	public static final Map<Integer, Data> ampTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> amp1uTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> comTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> fanTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> muxTable = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> opsTable = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> supTable = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> sw8Table = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> trpTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> trpRegTable = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> roadmTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> fanG8Table = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> shkTable = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> muxDemuxVoaTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> scmgrtTable = new TreeMap<Integer, Data>();

	public static final Map<Integer, Data> scmgrcTable = new TreeMap<Integer, Data>();
	public static final Map<Integer, Data> ppmTable = new TreeMap<Integer, Data>();
	private SerialNumber ne;
	private byte[] parameters;
	private int command;
	private int commandCode;
	private String name;

	static
	{
		add(amp1uTable, 301, 3, Msg.getString("Command.1"));
		add(amp1uTable, 302, 251, 
				Msg.getString("Command.22"));
		add(amp1uTable, 303, 218, 
				Msg.getString("Command.21"));
		add(amp1uTable, 304, 253, 
				Msg.getString("Command.20"));
		add(amp1uTable, 306, 217, 
				Msg.getString("Command.0"));
		add(amp1uTable, 307, 252, 
				Msg.getString("Command.19"));
		add(amp1uTable, 308, 
				242, Msg.getString("Command.6"));
		add(amp1uTable, 309, 
				243, Msg.getString("Command.5"));
		add(amp1uTable, 310, 
				244, Msg.getString("Command.3"));
		add(amp1uTable, 311, 245, 
				Msg.getString("Command.4"));
		add(amp1uTable, 312, 246, 
				Msg.getString("Command.2"));
		add(amp1uTable, 313, 250, 
				Msg.getString("Command.7"));
		add(amp1uTable, 314, 249, 
				Msg.getString("Command.8"));
		add(amp1uTable, 315, 248, 
				Msg.getString("Command.9"));
		add(amp1uTable, 316, 247, 
				Msg.getString("Command.10"));
		add(amp1uTable, 318, 2, Msg.getString("Command.11"));
		add(amp1uTable, 323, 7, Msg.getString("Command.15"));
		add(amp1uTable, 324, 8, 
				Msg.getString("Command.16"));
		add(amp1uTable, 325, 215, 
				Msg.getString("Command.18"));
		add(amp1uTable, 326, 216, 
				Msg.getString("Command.17"));

		add(ampTable, 20, 58, 
				Msg.getString("Command.12"));
		add(ampTable, 19, 2, 
				Msg.getString("Command.13"));
		add(ampTable, 18, 1, 
				Msg.getString("Command.14"));
		add(ampTable, 402, 59, Msg.getString("Command.23"));
		add(ampTable, 403, 60, Msg.getString("Command.24"));
		add(ampTable, 404, 3, Msg.getString("Command.25"));
		add(ampTable, 405, 4, Msg.getString("Command.26"));
		add(ampTable, 801, 64, Msg.getString("Command.27"));
		add(ampTable, 802, 65, Msg.getString("Command.28"));
		add(ampTable, 803, 66, 
				Msg.getString("Command.29"));
		add(ampTable, 804, 67, 
				Msg.getString("Command.30"));
		add(ampTable, 807, 70, 
				Msg.getString("Command.31"));
		add(fanTable, 200, 2, 
				Msg.getString("Command.32"));
		add(fanTable, 201, 4, 
				Msg.getString("Command.33"));
		add(opsTable, 23, 8, Msg.getString("Command.34"));
		add(opsTable, 22, 7, Msg.getString("Command.35"));
		add(opsTable, 21, 6, Msg.getString("Command.36"));
		add(supTable, 42, 4, Msg.getString("Command.37"));
		add(supTable, 41, 3, Msg.getString("Command.38"));
		add(supTable, 53, 25, 
				Msg.getString("Command.39"));
		add(supTable, 54, 32794, 
				Msg.getString("Command.40"));

		add(supTable, 44, 7, Msg.getString("Command.41"));
		add(supTable, 40, 22, Msg.getString("Command.42"));
		add(supTable, 47, 20, 
				Msg.getString("Command.43"));
		add(supTable, 48, 21, Msg.getString("Command.44"));
		add(supTable, 45, 18, Msg.getString("Command.45"));
		add(supTable, 329, 24, 
				Msg.getString("Command.46"));
		add(supTable, 43, 5, Msg.getString("Command.47"));
		add(supTable, 46, 19, Msg.getString("Command.48"));
		add(supTable, 51, 21, 
				Msg.getString("Command.49"));
		add(supTable, 150, 23, 
				Msg.getString("Command.50"));
		add(supTable, 52, 38, 
				Msg.getString("Command.51"));
		add(supTable, 843, 27, 
				Msg.getString("Command.52"));
		add(supTable, 844, 28, 
				Msg.getString("Command.53"));
		add(supTable, 861, 32, 
				Msg.getString("Command.591"));
		add(sw8Table, 24, 2, 
				Msg.getString("Command.54"));
		add(sw8Table, 25, 4, 
				Msg.getString("Command.55"));
		add(sw8Table, 26, 6, Msg.getString("Command.56"));

		add(trpTable, TRP_FEC2_RX_OFF, TRP_FEC2_RX_OFF_CODE, 
				Msg.getString("Command.61"));
		add(trpTable, 113, -56, 
				Msg.getString("Command.62"));
		add(trpTable, 110, -59, 
				Msg.getString("Command.63"));
		add(trpTable, 111, -58, 
				Msg.getString("Command.64"));
		add(trpTable, 11, -89,	Msg.getString("Command.65"));
		add(trpTable, 12, -88, 
				Msg.getString("Command.66"));
		add(trpTable, 9, -91, 
				Msg.getString("Command.67"));
		add(trpTable, 10, -90, 
				Msg.getString("Command.68"));
		add(trpTable, 8, -76, 
				Msg.getString("Command.69"));
		add(trpTable, 6, -78, 
				Msg.getString("Command.70"));
		add(trpTable, 7, -77, 
				Msg.getString("Command.71"));
		add(trpTable, 5, -79, 
				Msg.getString("Command.72"));
		add(trpTable, 109, -52, 
				Msg.getString("Command.73"));
		add(trpTable, 107, -54, 
				Msg.getString("Command.74"));
		add(trpTable, 104, -67, 
				Msg.getString("Command.75"));
		add(trpTable, 102, -69, 
				Msg.getString("Command.76"));
		add(trpTable, 108, -53, 
				Msg.getString("Command.77"));
		add(trpTable, 106, -55, 
				Msg.getString("Command.78"));
		add(trpTable, 103, -68, 
				Msg.getString("Command.79"));
		add(trpTable, 101, -70, 
				Msg.getString("Command.80"));
		add(trpTable, 3, 160, 
				Msg.getString("Command.81"));
		add(trpTable, 16, 4, 
				Msg.getString("Command.82"));
		add(trpTable, 15, 3, 
				Msg.getString("Command.83"));
		add(trpTable, 2, 2, 
				Msg.getString("Command.84"));
		add(trpTable, 1, 1, 
				Msg.getString("Command.85"));
		add(trpTable, 4, 33, Msg.getString("Command.86"));
		add(trpTable, 105, -61, 
				Msg.getString("Command.87"));
		add(trpTable, 14, -72, 
				Msg.getString("Command.88"));
		add(trpTable, 13, -73, 
				Msg.getString("Command.89"));
		add(trpTable, 400, 15, 
				Msg.getString("Command.90"));
		add(trpTable, 401, 47, 
				Msg.getString("Command.91"));
		add(trpTable, 131, 244, 
				Msg.getString("Command.92"));
		add(trpTable, 845, 250, 
				Msg.getString("Command.93"));
		add(trpTable, 132, 246, 
				Msg.getString("Command.94"));
		add(trpTable, 133, 248, 
				Msg.getString("Command.95"));
		add(trpTable, 134, 173, 
				Msg.getString("JOTNSDH.18"));
		add(trpTable, 135, 174, 
				Msg.getString("JOTNSDH.19"));
		add(trpTable, 136, 
				175, Msg.getString("Command.96"));
		add(trpTable, 137, 
				176, Msg.getString("Command.97"));

		add(trpTable, 853, 
				151, Msg.getString("Command.581"));
		add(trpTable, 852, 
				150, Msg.getString("Command.582"));
		add(trpTable, 851, 
				145, Msg.getString("Command.583"));
		add(trpTable, 850, 
				144, Msg.getString("Command.584"));
		add(trpTable, 855, 
				147, Msg.getString("Command.585"));
		add(trpTable, 854, 
				146, Msg.getString("Command.586"));
		add(trpTable, 856, 
				152, Msg.getString("Command.587"));
		add(trpTable, 857, 
				154, Msg.getString("Command.588"));
		add(trpTable, 859, 
				149, Msg.getString("Command.589"));
		add(trpTable, 858, 
				148, Msg.getString("Command.590"));
		add(trpTable, 860, 
				159, Msg.getString("Command.592"));

		trpRegTable.putAll(trpTable);
		comTable.putAll(trpTable);
		muxTable.putAll(trpTable);

		add(trpTable, 114, -86, 
				Msg.getString("Command.59"));
		add(trpTable, 115, -87, 
				Msg.getString("Command.60"));
		add(trpTable, 116, -84, 
				Msg.getString("Command.108"));
		add(trpTable, 117, -85, 
				Msg.getString("Command.107"));

		add(trpRegTable, 846, -86, 
				Msg.getString("Command.576"));
		add(trpRegTable, 847, -87, 
				Msg.getString("Command.577"));
		add(trpRegTable, 127, -84, 
				Msg.getString("Command.57"));
		add(trpRegTable, 128, -85, 
				Msg.getString("Command.58"));

		add(muxTable, 118, 227, 
				Msg.getString("Command.98"));
		add(muxTable, 119, 228, 
				Msg.getString("Command.99"));
		add(muxTable, 120, 229, 
				Msg.getString("Command.100"));
		add(muxTable, 121, 230, 
				Msg.getString("Command.101"));

		add(comTable, 124, 5, 
				Msg.getString("Command.102"));
		add(comTable, 123, 4, Msg.getString("Command.103"));
		add(comTable, 122, 3, Msg.getString("Command.104"));
		add(comTable, 129, 76, 
				Msg.getString("Command.105"));
		add(comTable, 130, 77, 
				Msg.getString("Command.106"));
		add(comTable, 126, 171, 
				Msg.getString("Command.107"));
		add(comTable, 125, 172, 
				Msg.getString("Command.108"));
		add(comTable, 138, 252, 
				Msg.getString("Command.578"));

		add(roadmTable, 756, 57600, 
				Msg.getString("Command.109"));
		add(roadmTable, 757, 
				57472, Msg.getString("Command.110"));
		add(roadmTable, 755, 
				58624, Msg.getString("Command.111"));
		add(roadmTable, 330, 
				58625, Msg.getString("Command.112"));
		add(roadmTable, 331, 
				58626, Msg.getString("Command.113"));
		add(roadmTable, 332, 
				58627, Msg.getString("Command.114"));
		add(roadmTable, 333, 
				58628, Msg.getString("Command.115"));
		add(roadmTable, 334, 
				58629, Msg.getString("Command.116"));
		add(roadmTable, 335, 
				58630, Msg.getString("Command.117"));
		add(roadmTable, 336, 
				58631, Msg.getString("Command.118"));
		add(roadmTable, 337, 
				58632, Msg.getString("Command.119"));
		add(roadmTable, 338, 
				58633, Msg.getString("Command.120"));
		add(roadmTable, 339, 
				58634, Msg.getString("Command.121"));
		add(roadmTable, 340, 
				58635, Msg.getString("Command.122"));
		add(roadmTable, 341, 
				58636, Msg.getString("Command.123"));
		add(roadmTable, 342, 
				58637, Msg.getString("Command.124"));
		add(roadmTable, 343, 
				58638, Msg.getString("Command.125"));
		add(roadmTable, 344, 
				58639, Msg.getString("Command.126"));
		add(roadmTable, 345, 
				58640, Msg.getString("Command.127"));
		add(roadmTable, 346, 
				58641, Msg.getString("Command.128"));
		add(roadmTable, 347, 
				58642, Msg.getString("Command.129"));
		add(roadmTable, 348, 
				58643, Msg.getString("Command.130"));
		add(roadmTable, 349, 
				58644, Msg.getString("Command.131"));
		add(roadmTable, 350, 
				58645, Msg.getString("Command.132"));
		add(roadmTable, 351, 
				58646, Msg.getString("Command.133"));
		add(roadmTable, 352, 
				58647, Msg.getString("Command.134"));
		add(roadmTable, 353, 
				58648, Msg.getString("Command.135"));
		add(roadmTable, 354, 
				58649, Msg.getString("Command.136"));
		add(roadmTable, 355, 
				58650, Msg.getString("Command.137"));
		add(roadmTable, 356, 
				58651, Msg.getString("Command.138"));
		add(roadmTable, 357, 
				58652, Msg.getString("Command.139"));
		add(roadmTable, 358, 
				58653, Msg.getString("Command.140"));
		add(roadmTable, 359, 
				58654, Msg.getString("Command.141"));
		add(roadmTable, 360, 
				58655, Msg.getString("Command.142"));
		add(roadmTable, 361, 
				58656, Msg.getString("Command.143"));
		add(roadmTable, 362, 
				58657, Msg.getString("Command.144"));
		add(roadmTable, 363, 
				58658, Msg.getString("Command.145"));
		add(roadmTable, 364, 
				58659, Msg.getString("Command.146"));
		add(roadmTable, 365, 
				58660, Msg.getString("Command.147"));
		add(roadmTable, 366, 
				58661, Msg.getString("Command.148"));
		add(roadmTable, 367, 
				58662, Msg.getString("Command.149"));
		add(roadmTable, 368, 
				58663, Msg.getString("Command.150"));
		add(roadmTable, 369, 
				58664, Msg.getString("Command.151"));

		add(roadmTable, 410, 57601, 
				Msg.getString("Command.152"));
		add(roadmTable, 411, 57602, 
				Msg.getString("Command.153"));
		add(roadmTable, 412, 57603, 
				Msg.getString("Command.154"));
		add(roadmTable, 413, 57604, 
				Msg.getString("Command.155"));
		add(roadmTable, 414, 57605, 
				Msg.getString("Command.156"));
		add(roadmTable, 415, 57606, 
				Msg.getString("Command.157"));
		add(roadmTable, 416, 57607, 
				Msg.getString("Command.158"));
		add(roadmTable, 417, 57608, 
				Msg.getString("Command.159"));
		add(roadmTable, 418, 57609, 
				Msg.getString("Command.160"));
		add(roadmTable, 419, 57610, 
				Msg.getString("Command.161"));
		add(roadmTable, 420, 57611, 
				Msg.getString("Command.162"));
		add(roadmTable, 421, 57612, 
				Msg.getString("Command.163"));
		add(roadmTable, 422, 57613, 
				Msg.getString("Command.164"));
		add(roadmTable, 423, 57614, 
				Msg.getString("Command.165"));
		add(roadmTable, 424, 57615, 
				Msg.getString("Command.166"));
		add(roadmTable, 425, 57616, 
				Msg.getString("Command.167"));
		add(roadmTable, 426, 57617, 
				Msg.getString("Command.168"));
		add(roadmTable, 427, 57618, 
				Msg.getString("Command.169"));
		add(roadmTable, 428, 57619, 
				Msg.getString("Command.170"));
		add(roadmTable, 429, 57620, 
				Msg.getString("Command.171"));
		add(roadmTable, 430, 57621, 
				Msg.getString("Command.172"));
		add(roadmTable, 431, 57622, 
				Msg.getString("Command.173"));
		add(roadmTable, 432, 57623, 
				Msg.getString("Command.174"));
		add(roadmTable, 433, 57624, 
				Msg.getString("Command.175"));
		add(roadmTable, 434, 57625, 
				Msg.getString("Command.176"));
		add(roadmTable, 435, 57626, 
				Msg.getString("Command.177"));
		add(roadmTable, 436, 57627, 
				Msg.getString("Command.178"));
		add(roadmTable, 437, 57628, 
				Msg.getString("Command.179"));
		add(roadmTable, 438, 57629, 
				Msg.getString("Command.180"));
		add(roadmTable, 439, 57630, 
				Msg.getString("Command.181"));
		add(roadmTable, 440, 57631, 
				Msg.getString("Command.182"));
		add(roadmTable, 441, 57632, 
				Msg.getString("Command.183"));
		add(roadmTable, 442, 57633, 
				Msg.getString("Command.184"));
		add(roadmTable, 443, 57634, 
				Msg.getString("Command.185"));
		add(roadmTable, 444, 57635, 
				Msg.getString("Command.186"));
		add(roadmTable, 445, 57636, 
				Msg.getString("Command.187"));
		add(roadmTable, 446, 57637, 
				Msg.getString("Command.188"));
		add(roadmTable, 447, 57638, 
				Msg.getString("Command.189"));
		add(roadmTable, 448, 57639, 
				Msg.getString("Command.190"));
		add(roadmTable, 449, 57640, 
				Msg.getString("Command.191"));

		add(roadmTable, 450, 
				57473, Msg.getString("Command.192"));
		add(roadmTable, 451, 
				57474, Msg.getString("Command.193"));
		add(roadmTable, 452, 
				57475, Msg.getString("Command.194"));
		add(roadmTable, 453, 
				57476, Msg.getString("Command.195"));
		add(roadmTable, 454, 
				57477, Msg.getString("Command.196"));
		add(roadmTable, 455, 
				57478, Msg.getString("Command.197"));
		add(roadmTable, 456, 
				57479, Msg.getString("Command.198"));
		add(roadmTable, 457, 
				57480, Msg.getString("Command.199"));
		add(roadmTable, 458, 
				57481, Msg.getString("Command.200"));
		add(roadmTable, 459, 
				57482, Msg.getString("Command.201"));
		add(roadmTable, 460, 
				57483, Msg.getString("Command.202"));
		add(roadmTable, 461, 
				57484, Msg.getString("Command.203"));
		add(roadmTable, 462, 
				57485, Msg.getString("Command.204"));
		add(roadmTable, 463, 
				57486, Msg.getString("Command.205"));
		add(roadmTable, 464, 
				57487, Msg.getString("Command.206"));
		add(roadmTable, 465, 
				57488, Msg.getString("Command.207"));
		add(roadmTable, 466, 
				57489, Msg.getString("Command.208"));
		add(roadmTable, 467, 
				57490, Msg.getString("Command.209"));
		add(roadmTable, 468, 
				57491, Msg.getString("Command.210"));
		add(roadmTable, 469, 
				57492, Msg.getString("Command.211"));
		add(roadmTable, 470, 
				57493, Msg.getString("Command.212"));
		add(roadmTable, 471, 
				57494, Msg.getString("Command.213"));
		add(roadmTable, 472, 
				57495, Msg.getString("Command.214"));
		add(roadmTable, 473, 
				57496, Msg.getString("Command.215"));
		add(roadmTable, 474, 
				57497, Msg.getString("Command.216"));
		add(roadmTable, 475, 
				57498, Msg.getString("Command.217"));
		add(roadmTable, 476, 
				57499, Msg.getString("Command.218"));
		add(roadmTable, 477, 
				57500, Msg.getString("Command.219"));
		add(roadmTable, 478, 
				57501, Msg.getString("Command.220"));
		add(roadmTable, 479, 
				57502, Msg.getString("Command.221"));
		add(roadmTable, 480, 
				57503, Msg.getString("Command.222"));
		add(roadmTable, 481, 
				57504, Msg.getString("Command.223"));
		add(roadmTable, 482, 
				57505, Msg.getString("Command.224"));
		add(roadmTable, 483, 
				57506, Msg.getString("Command.225"));
		add(roadmTable, 484, 
				57507, Msg.getString("Command.226"));
		add(roadmTable, 485, 
				57508, Msg.getString("Command.227"));
		add(roadmTable, 486, 
				57509, Msg.getString("Command.228"));
		add(roadmTable, 487, 
				57510, Msg.getString("Command.229"));
		add(roadmTable, 488, 
				57511, Msg.getString("Command.230"));
		add(roadmTable, 489, 
				57512, Msg.getString("Command.231"));

		add(roadmTable, 490, 
				58753, Msg.getString("Command.232"));
		add(roadmTable, 491, 
				58754, Msg.getString("Command.233"));
		add(roadmTable, 492, 
				58755, Msg.getString("Command.234"));
		add(roadmTable, 493, 
				58756, Msg.getString("Command.235"));
		add(roadmTable, 494, 
				58757, Msg.getString("Command.236"));
		add(roadmTable, 495, 
				58758, Msg.getString("Command.237"));
		add(roadmTable, 496, 
				58759, Msg.getString("Command.238"));
		add(roadmTable, 497, 
				58760, Msg.getString("Command.239"));
		add(roadmTable, 498, 
				58761, Msg.getString("Command.240"));
		add(roadmTable, 499, 
				58762, Msg.getString("Command.241"));
		add(roadmTable, 500, 
				58763, Msg.getString("Command.242"));
		add(roadmTable, 501, 
				58764, Msg.getString("Command.243"));
		add(roadmTable, 502, 
				58765, Msg.getString("Command.244"));
		add(roadmTable, 503, 
				58766, Msg.getString("Command.245"));
		add(roadmTable, 504, 
				58767, Msg.getString("Command.246"));
		add(roadmTable, 505, 
				58768, Msg.getString("Command.247"));
		add(roadmTable, 506, 
				58769, Msg.getString("Command.248"));
		add(roadmTable, 507, 
				58770, Msg.getString("Command.249"));
		add(roadmTable, 508, 
				58771, Msg.getString("Command.250"));
		add(roadmTable, 509, 
				58772, Msg.getString("Command.251"));
		add(roadmTable, 510, 
				58773, Msg.getString("Command.252"));
		add(roadmTable, 511, 
				58774, Msg.getString("Command.253"));
		add(roadmTable, 512, 
				58775, Msg.getString("Command.254"));
		add(roadmTable, 513, 
				58776, Msg.getString("Command.255"));
		add(roadmTable, 514, 
				58777, Msg.getString("Command.256"));
		add(roadmTable, 515, 
				58778, Msg.getString("Command.257"));
		add(roadmTable, 516, 
				58779, Msg.getString("Command.258"));
		add(roadmTable, 517, 
				58780, Msg.getString("Command.259"));
		add(roadmTable, 518, 
				58781, Msg.getString("Command.260"));
		add(roadmTable, 519, 
				58782, Msg.getString("Command.261"));
		add(roadmTable, 520, 
				58783, Msg.getString("Command.262"));
		add(roadmTable, 521, 
				58784, Msg.getString("Command.263"));
		add(roadmTable, 522, 
				58785, Msg.getString("Command.264"));
		add(roadmTable, 523, 
				58786, Msg.getString("Command.265"));
		add(roadmTable, 524, 
				58787, Msg.getString("Command.266"));
		add(roadmTable, 525, 
				58788, Msg.getString("Command.267"));
		add(roadmTable, 526, 
				58789, Msg.getString("Command.268"));
		add(roadmTable, 527, 
				58790, Msg.getString("Command.269"));
		add(roadmTable, 528, 
				58791, Msg.getString("Command.270"));
		add(roadmTable, 529, 
				58792, Msg.getString("Command.271"));
		add(roadmTable, 530, 
				58752, 
				Msg.getString("Command.272"));

		add(roadmTable, 531, 
				58881, Msg.getString("Command.273"));
		add(roadmTable, 532, 
				58882, Msg.getString("Command.274"));
		add(roadmTable, 533, 
				58883, Msg.getString("Command.275"));
		add(roadmTable, 534, 
				58884, Msg.getString("Command.276"));
		add(roadmTable, 535, 
				58885, Msg.getString("Command.277"));
		add(roadmTable, 536, 
				58886, Msg.getString("Command.278"));
		add(roadmTable, 537, 
				58887, Msg.getString("Command.279"));
		add(roadmTable, 538, 
				58888, Msg.getString("Command.280"));
		add(roadmTable, 539, 
				58889, Msg.getString("Command.281"));
		add(roadmTable, 540, 
				58890, Msg.getString("Command.282"));
		add(roadmTable, 541, 
				58891, Msg.getString("Command.283"));
		add(roadmTable, 542, 
				58892, Msg.getString("Command.284"));
		add(roadmTable, 543, 
				58893, Msg.getString("Command.285"));
		add(roadmTable, 544, 
				58894, Msg.getString("Command.286"));
		add(roadmTable, 545, 
				58895, Msg.getString("Command.287"));
		add(roadmTable, 546, 
				58896, Msg.getString("Command.288"));
		add(roadmTable, 547, 
				58897, Msg.getString("Command.289"));
		add(roadmTable, 548, 
				58898, Msg.getString("Command.290"));
		add(roadmTable, 549, 
				58899, Msg.getString("Command.291"));
		add(roadmTable, 550, 
				58900, Msg.getString("Command.292"));
		add(roadmTable, 551, 
				58901, Msg.getString("Command.293"));
		add(roadmTable, 552, 
				58902, Msg.getString("Command.294"));
		add(roadmTable, 553, 
				58903, Msg.getString("Command.295"));
		add(roadmTable, 554, 
				58904, Msg.getString("Command.296"));
		add(roadmTable, 555, 
				58905, Msg.getString("Command.297"));
		add(roadmTable, 556, 
				58906, Msg.getString("Command.298"));
		add(roadmTable, 557, 
				58907, Msg.getString("Command.299"));
		add(roadmTable, 558, 
				58908, Msg.getString("Command.300"));
		add(roadmTable, 559, 
				58909, Msg.getString("Command.301"));
		add(roadmTable, 560, 
				58910, Msg.getString("Command.302"));
		add(roadmTable, 561, 
				58911, Msg.getString("Command.303"));
		add(roadmTable, 562, 
				58912, Msg.getString("Command.304"));
		add(roadmTable, 563, 
				58913, Msg.getString("Command.305"));
		add(roadmTable, 564, 
				58914, Msg.getString("Command.306"));
		add(roadmTable, 565, 
				58915, Msg.getString("Command.307"));
		add(roadmTable, 566, 
				58916, Msg.getString("Command.308"));
		add(roadmTable, 567, 
				58917, Msg.getString("Command.309"));
		add(roadmTable, 568, 
				58918, Msg.getString("Command.310"));
		add(roadmTable, 569, 
				58919, Msg.getString("Command.311"));
		add(roadmTable, 570, 
				58920, Msg.getString("Command.312"));
		add(roadmTable, 571, 
				58880, 
				Msg.getString("Command.313"));

		add(roadmTable, 572, 
				58497, Msg.getString("Command.314"));
		add(roadmTable, 573, 
				58498, Msg.getString("Command.315"));
		add(roadmTable, 574, 
				58499, Msg.getString("Command.316"));
		add(roadmTable, 575, 
				58500, Msg.getString("Command.317"));
		add(roadmTable, 576, 
				58501, Msg.getString("Command.318"));
		add(roadmTable, 577, 
				58502, Msg.getString("Command.319"));
		add(roadmTable, 578, 
				58503, Msg.getString("Command.320"));
		add(roadmTable, 579, 
				58504, Msg.getString("Command.321"));
		add(roadmTable, 580, 
				58505, Msg.getString("Command.322"));
		add(roadmTable, 581, 
				58506, Msg.getString("Command.323"));
		add(roadmTable, 582, 
				58507, Msg.getString("Command.324"));
		add(roadmTable, 583, 
				58508, Msg.getString("Command.325"));
		add(roadmTable, 584, 
				58509, Msg.getString("Command.326"));
		add(roadmTable, 585, 
				58510, Msg.getString("Command.327"));
		add(roadmTable, 586, 
				58511, Msg.getString("Command.328"));
		add(roadmTable, 587, 
				58512, Msg.getString("Command.329"));
		add(roadmTable, 588, 
				58513, Msg.getString("Command.330"));
		add(roadmTable, 589, 
				58514, Msg.getString("Command.331"));
		add(roadmTable, 590, 
				58515, Msg.getString("Command.332"));
		add(roadmTable, 591, 
				58516, Msg.getString("Command.333"));
		add(roadmTable, 592, 
				58517, Msg.getString("Command.334"));
		add(roadmTable, 593, 
				58518, Msg.getString("Command.335"));
		add(roadmTable, 594, 
				58519, Msg.getString("Command.336"));
		add(roadmTable, 595, 
				58520, Msg.getString("Command.337"));
		add(roadmTable, 596, 
				58521, Msg.getString("Command.338"));
		add(roadmTable, 597, 
				58522, Msg.getString("Command.339"));
		add(roadmTable, 598, 
				58523, Msg.getString("Command.340"));
		add(roadmTable, 599, 
				58524, Msg.getString("Command.341"));
		add(roadmTable, 600, 
				58525, Msg.getString("Command.342"));
		add(roadmTable, 601, 
				58526, Msg.getString("Command.343"));
		add(roadmTable, 602, 
				58527, Msg.getString("Command.344"));
		add(roadmTable, 603, 
				58528, Msg.getString("Command.345"));
		add(roadmTable, 604, 
				58529, Msg.getString("Command.346"));
		add(roadmTable, 605, 
				58530, Msg.getString("Command.347"));
		add(roadmTable, 606, 
				58531, Msg.getString("Command.348"));
		add(roadmTable, 607, 
				58532, Msg.getString("Command.349"));
		add(roadmTable, 608, 
				58533, Msg.getString("Command.350"));
		add(roadmTable, 609, 
				58534, Msg.getString("Command.351"));
		add(roadmTable, 610, 
				58535, Msg.getString("Command.352"));
		add(roadmTable, 611, 
				58536, Msg.getString("Command.353"));
		add(roadmTable, 612, 
				58496, 
				Msg.getString("Command.354"));

		add(roadmTable, 613, 57345, 
				Msg.getString("Command.355"));
		add(roadmTable, 614, 57346, 
				Msg.getString("Command.356"));
		add(roadmTable, 615, 57347, 
				Msg.getString("Command.357"));
		add(roadmTable, 616, 57348, 
				Msg.getString("Command.358"));
		add(roadmTable, 617, 57349, 
				Msg.getString("Command.359"));
		add(roadmTable, 618, 57350, 
				Msg.getString("Command.360"));
		add(roadmTable, 619, 57351, 
				Msg.getString("Command.361"));
		add(roadmTable, 620, 57352, 
				Msg.getString("Command.362"));
		add(roadmTable, 621, 57353, 
				Msg.getString("Command.363"));
		add(roadmTable, 622, 57354, 
				Msg.getString("Command.364"));
		add(roadmTable, 623, 57355, 
				Msg.getString("Command.365"));
		add(roadmTable, 624, 57356, 
				Msg.getString("Command.366"));
		add(roadmTable, 625, 57357, 
				Msg.getString("Command.367"));
		add(roadmTable, 626, 57358, 
				Msg.getString("Command.368"));
		add(roadmTable, 627, 57359, 
				Msg.getString("Command.369"));
		add(roadmTable, 628, 57360, 
				Msg.getString("Command.370"));
		add(roadmTable, 629, 57361, 
				Msg.getString("Command.371"));
		add(roadmTable, 630, 57362, 
				Msg.getString("Command.372"));
		add(roadmTable, 631, 57363, 
				Msg.getString("Command.373"));
		add(roadmTable, 632, 57364, 
				Msg.getString("Command.374"));
		add(roadmTable, 633, 57365, 
				Msg.getString("Command.375"));
		add(roadmTable, 634, 57366, 
				Msg.getString("Command.376"));
		add(roadmTable, 635, 57367, 
				Msg.getString("Command.377"));
		add(roadmTable, 636, 57368, 
				Msg.getString("Command.378"));
		add(roadmTable, 637, 57369, 
				Msg.getString("Command.379"));
		add(roadmTable, 638, 57370, 
				Msg.getString("Command.380"));
		add(roadmTable, 639, 57371, 
				Msg.getString("Command.381"));
		add(roadmTable, 640, 57372, 
				Msg.getString("Command.382"));
		add(roadmTable, 641, 57373, 
				Msg.getString("Command.383"));
		add(roadmTable, 642, 57374, 
				Msg.getString("Command.384"));
		add(roadmTable, 643, 57375, 
				Msg.getString("Command.385"));
		add(roadmTable, 644, 57376, 
				Msg.getString("Command.386"));
		add(roadmTable, 645, 57377, 
				Msg.getString("Command.387"));
		add(roadmTable, 646, 57378, 
				Msg.getString("Command.388"));
		add(roadmTable, 647, 57379, 
				Msg.getString("Command.389"));
		add(roadmTable, 648, 57380, 
				Msg.getString("Command.390"));
		add(roadmTable, 649, 57381, 
				Msg.getString("Command.391"));
		add(roadmTable, 650, 57382, 
				Msg.getString("Command.392"));
		add(roadmTable, 651, 57383, 
				Msg.getString("Command.393"));
		add(roadmTable, 652, 57384, 
				Msg.getString("Command.394"));
		add(roadmTable, 653, 
				57344, Msg.getString("Command.395"));

		add(roadmTable, 655, 
				59137, Msg.getString("Command.396"));
		add(roadmTable, 656, 
				59138, Msg.getString("Command.397"));
		add(roadmTable, 657, 
				59139, Msg.getString("Command.398"));
		add(roadmTable, 658, 
				59140, Msg.getString("Command.399"));
		add(roadmTable, 659, 
				59141, Msg.getString("Command.400"));
		add(roadmTable, 660, 
				59142, Msg.getString("Command.401"));
		add(roadmTable, 661, 
				59143, Msg.getString("Command.402"));
		add(roadmTable, 662, 
				59144, Msg.getString("Command.403"));
		add(roadmTable, 663, 
				59145, Msg.getString("Command.404"));
		add(roadmTable, 664, 
				59146, Msg.getString("Command.405"));
		add(roadmTable, 665, 
				59147, Msg.getString("Command.406"));
		add(roadmTable, 666, 
				59148, Msg.getString("Command.407"));
		add(roadmTable, 667, 
				59149, Msg.getString("Command.408"));
		add(roadmTable, 668, 
				59150, Msg.getString("Command.409"));
		add(roadmTable, 669, 
				59151, Msg.getString("Command.410"));
		add(roadmTable, 670, 
				59152, Msg.getString("Command.411"));
		add(roadmTable, 671, 
				59153, Msg.getString("Command.412"));
		add(roadmTable, 672, 
				59154, Msg.getString("Command.413"));
		add(roadmTable, 673, 
				59155, Msg.getString("Command.414"));
		add(roadmTable, 674, 
				59156, Msg.getString("Command.415"));
		add(roadmTable, 675, 
				59157, Msg.getString("Command.416"));
		add(roadmTable, 676, 
				59158, Msg.getString("Command.417"));
		add(roadmTable, 677, 
				59159, Msg.getString("Command.418"));
		add(roadmTable, 678, 
				59160, Msg.getString("Command.419"));
		add(roadmTable, 679, 
				59161, Msg.getString("Command.420"));
		add(roadmTable, 680, 
				59162, Msg.getString("Command.421"));
		add(roadmTable, 681, 
				59163, Msg.getString("Command.422"));
		add(roadmTable, 682, 
				59164, Msg.getString("Command.423"));
		add(roadmTable, 683, 
				59165, Msg.getString("Command.424"));
		add(roadmTable, 684, 
				59166, Msg.getString("Command.425"));
		add(roadmTable, 685, 
				59167, Msg.getString("Command.426"));
		add(roadmTable, 686, 
				59168, Msg.getString("Command.427"));
		add(roadmTable, 687, 
				59169, Msg.getString("Command.428"));
		add(roadmTable, 688, 
				59170, Msg.getString("Command.429"));
		add(roadmTable, 689, 
				59171, Msg.getString("Command.430"));
		add(roadmTable, 690, 
				59172, Msg.getString("Command.431"));
		add(roadmTable, 691, 
				59173, Msg.getString("Command.432"));
		add(roadmTable, 692, 
				59174, Msg.getString("Command.433"));
		add(roadmTable, 693, 
				59175, Msg.getString("Command.434"));
		add(roadmTable, 694, 
				59176, Msg.getString("Command.435"));
		add(roadmTable, 654, 59136, 
				Msg.getString("Command.436"));

		add(roadmTable, 714, 
				59265, Msg.getString("Command.437"));
		add(roadmTable, 715, 
				59266, Msg.getString("Command.438"));
		add(roadmTable, 716, 
				59267, Msg.getString("Command.439"));
		add(roadmTable, 717, 
				59268, Msg.getString("Command.440"));
		add(roadmTable, 718, 
				59269, Msg.getString("Command.441"));
		add(roadmTable, 719, 
				59270, Msg.getString("Command.442"));
		add(roadmTable, 720, 
				59271, Msg.getString("Command.443"));
		add(roadmTable, 721, 
				59272, Msg.getString("Command.444"));
		add(roadmTable, 722, 
				59273, Msg.getString("Command.445"));
		add(roadmTable, 723, 
				59274, Msg.getString("Command.446"));
		add(roadmTable, 724, 
				59275, Msg.getString("Command.447"));
		add(roadmTable, 725, 
				59276, Msg.getString("Command.448"));
		add(roadmTable, 726, 
				59277, Msg.getString("Command.449"));
		add(roadmTable, 727, 
				59278, Msg.getString("Command.450"));
		add(roadmTable, 728, 
				59279, Msg.getString("Command.451"));
		add(roadmTable, 729, 
				59280, Msg.getString("Command.452"));
		add(roadmTable, 730, 
				59281, Msg.getString("Command.453"));
		add(roadmTable, 731, 
				59282, Msg.getString("Command.454"));
		add(roadmTable, 732, 
				59283, Msg.getString("Command.455"));
		add(roadmTable, 733, 
				59284, Msg.getString("Command.456"));
		add(roadmTable, 734, 
				59285, Msg.getString("Command.457"));
		add(roadmTable, 735, 
				59286, Msg.getString("Command.458"));
		add(roadmTable, 736, 
				59287, Msg.getString("Command.459"));
		add(roadmTable, 737, 
				59288, Msg.getString("Command.460"));
		add(roadmTable, 738, 
				59289, Msg.getString("Command.461"));
		add(roadmTable, 739, 
				59290, Msg.getString("Command.462"));
		add(roadmTable, 740, 
				59291, Msg.getString("Command.463"));
		add(roadmTable, 741, 
				59292, Msg.getString("Command.464"));
		add(roadmTable, 742, 
				59293, Msg.getString("Command.465"));
		add(roadmTable, 743, 
				59294, Msg.getString("Command.466"));
		add(roadmTable, 744, 
				59295, Msg.getString("Command.467"));
		add(roadmTable, 745, 
				59296, Msg.getString("Command.468"));
		add(roadmTable, 746, 
				59297, Msg.getString("Command.469"));
		add(roadmTable, 747, 
				59298, Msg.getString("Command.470"));
		add(roadmTable, 748, 
				59299, Msg.getString("Command.471"));
		add(roadmTable, 749, 
				59300, Msg.getString("Command.472"));
		add(roadmTable, 750, 
				59301, Msg.getString("Command.473"));
		add(roadmTable, 751, 
				59302, Msg.getString("Command.474"));
		add(roadmTable, 752, 
				59303, Msg.getString("Command.475"));
		add(roadmTable, 753, 
				59304, Msg.getString("Command.476"));
		add(roadmTable, 713, 59264, 
				Msg.getString("Command.477"));

		add(roadmTable, 754, 59392, 
				Msg.getString("Command.478"));
		add(roadmTable, 758, 
				59520, Msg.getString("Command.479"));
		add(roadmTable, 370, 58240, 
				Msg.getString("Command.480"));
		add(roadmTable, 712, 
				59008, Msg.getString("Command.481"));
		add(roadmTable, 371, 
				58368, Msg.getString("Command.482"));
		add(roadmTable, 372, 57984, 
				Msg.getString("Command.483"));
		add(roadmTable, 373, 58112, 
				Msg.getString("Command.484"));
		add(roadmTable, 374, 
				57856, Msg.getString("Command.485"));
		add(roadmTable, 375, 57728, 
				Msg.getString("Command.486"));

		add(fanG8Table, 703, 33280, 
				Msg.getString("Command.487"));
		add(fanG8Table, 704, 33281, 
				Msg.getString("Command.488"));
		add(fanG8Table, 705, 33282, 
				Msg.getString("Command.489"));
		add(fanG8Table, 706, 33283, 
				Msg.getString("Command.490"));
		add(fanG8Table, 707, 33284, 
				Msg.getString("Command.491"));
		add(fanG8Table, 708, 33285, 
				Msg.getString("Command.492"));
		add(fanG8Table, 709, 33286, 
				Msg.getString("Command.493"));
		add(fanG8Table, 710, 33287, 
				Msg.getString("Command.494"));
		add(fanG8Table, 711, 33288, 
				Msg.getString("Command.495"));
		add(fanG8Table, 700, 
				33024, Msg.getString("Command.496"));
		add(fanG8Table, 701, 
				33025, Msg.getString("Command.497"));
		add(fanG8Table, 702, 
				33026, Msg.getString("Command.498"));
		add(fanG8Table, 849, 34048, 
				Msg.getString("Command.579"));
		add(fanG8Table, 848, 34304, 
				Msg.getString("Command.580"));

		add(muxDemuxVoaTable, 760, 
				57344, Msg.getString("Command.499"));
		add(muxDemuxVoaTable, 761, 
				57345, Msg.getString("Command.500"));
		add(muxDemuxVoaTable, 762, 
				57346, Msg.getString("Command.501"));
		add(muxDemuxVoaTable, 763, 
				57347, Msg.getString("Command.502"));
		add(muxDemuxVoaTable, 764, 
				57348, Msg.getString("Command.503"));
		add(muxDemuxVoaTable, 765, 
				57349, Msg.getString("Command.504"));
		add(muxDemuxVoaTable, 766, 
				57350, Msg.getString("Command.505"));
		add(muxDemuxVoaTable, 767, 
				57351, Msg.getString("Command.506"));
		add(muxDemuxVoaTable, 768, 
				57352, Msg.getString("Command.507"));
		add(muxDemuxVoaTable, 769, 
				57353, Msg.getString("Command.508"));
		add(muxDemuxVoaTable, 770, 
				57354, Msg.getString("Command.509"));
		add(muxDemuxVoaTable, 771, 
				57355, Msg.getString("Command.510"));
		add(muxDemuxVoaTable, 772, 
				57356, Msg.getString("Command.511"));
		add(muxDemuxVoaTable, 773, 
				57357, Msg.getString("Command.512"));
		add(muxDemuxVoaTable, 774, 
				57358, Msg.getString("Command.513"));
		add(muxDemuxVoaTable, 775, 
				57359, Msg.getString("Command.514"));
		add(muxDemuxVoaTable, 776, 
				57360, Msg.getString("Command.515"));
		add(muxDemuxVoaTable, 777, 
				57361, Msg.getString("Command.516"));
		add(muxDemuxVoaTable, 778, 
				57362, Msg.getString("Command.517"));
		add(muxDemuxVoaTable, 779, 
				57363, Msg.getString("Command.518"));
		add(muxDemuxVoaTable, 780, 
				57364, Msg.getString("Command.519"));
		add(muxDemuxVoaTable, 781, 
				57365, Msg.getString("Command.520"));
		add(muxDemuxVoaTable, 782, 
				57366, Msg.getString("Command.521"));
		add(muxDemuxVoaTable, 783, 
				57367, Msg.getString("Command.522"));
		add(muxDemuxVoaTable, 784, 
				57368, Msg.getString("Command.523"));
		add(muxDemuxVoaTable, 785, 
				57369, Msg.getString("Command.524"));
		add(muxDemuxVoaTable, 786, 
				57370, Msg.getString("Command.525"));
		add(muxDemuxVoaTable, 787, 
				57371, Msg.getString("Command.526"));
		add(muxDemuxVoaTable, 788, 
				57372, Msg.getString("Command.527"));
		add(muxDemuxVoaTable, 789, 
				57373, Msg.getString("Command.528"));
		add(muxDemuxVoaTable, 790, 
				57374, Msg.getString("Command.529"));
		add(muxDemuxVoaTable, 791, 
				57375, Msg.getString("Command.530"));
		add(muxDemuxVoaTable, 792, 
				57376, Msg.getString("Command.531"));
		add(muxDemuxVoaTable, 793, 
				57377, Msg.getString("Command.532"));
		add(muxDemuxVoaTable, 794, 
				57378, Msg.getString("Command.533"));
		add(muxDemuxVoaTable, 795, 
				57379, Msg.getString("Command.534"));
		add(muxDemuxVoaTable, 796, 
				57380, Msg.getString("Command.535"));
		add(muxDemuxVoaTable, 797, 
				57381, Msg.getString("Command.536"));
		add(muxDemuxVoaTable, 798, 
				57382, Msg.getString("Command.537"));
		add(muxDemuxVoaTable, 799, 
				57383, Msg.getString("Command.538"));
		add(muxDemuxVoaTable, 800, 
				57384, Msg.getString("Command.539"));

		add(ppmTable, 808, 57600, 
				Msg.getString("Command.540"));
		add(ppmTable, 809, 
				57601, 
				Msg.getString("Command.541"));
		add(ppmTable, 810, 
				57602, Msg.getString("Command.542"));
		add(ppmTable, 811, 
				57603, Msg.getString("Command.543"));
		add(ppmTable, 812, 
				57604, Msg.getString("Command.544"));
		add(ppmTable, 813, 
				57605, Msg.getString("Command.545"));
		add(ppmTable, 814, 
				57606, Msg.getString("Command.546"));
		add(ppmTable, 815, 
				57607, Msg.getString("Command.547"));
		add(ppmTable, 816, 
				57608, Msg.getString("Command.548"));
		add(ppmTable, 817, 
				57609, Msg.getString("Command.549"));
		add(ppmTable, 818, 
				921754, Msg.getString("Command.550"));
		add(ppmTable, 819, 
				57611, Msg.getString("Command.551"));
		add(ppmTable, 820, 
				57612, Msg.getString("Command.552"));
		add(ppmTable, 821, 
				57613, Msg.getString("Command.553"));
		add(ppmTable, 822, 
				57614, Msg.getString("Command.554"));
		add(ppmTable, 823, 
				57615, Msg.getString("Command.555"));
		add(ppmTable, 824, 
				57616, Msg.getString("Command.556"));

		add(ppmTable, 825, 57856, 
				Msg.getString("Command.557"));
		add(ppmTable, 826, 
				57857, 
				Msg.getString("Command.558"));
		add(ppmTable, 827, 
				57858, Msg.getString("Command.559"));
		add(ppmTable, 828, 
				57859, Msg.getString("Command.560"));
		add(ppmTable, 829, 
				57860, Msg.getString("Command.561"));
		add(ppmTable, 830, 
				57861, Msg.getString("Command.562"));
		add(ppmTable, 831, 
				57862, Msg.getString("Command.563"));
		add(ppmTable, 832, 
				57863, Msg.getString("Command.564"));
		add(ppmTable, 833, 
				57864, Msg.getString("Command.565"));
		add(ppmTable, 834, 
				57865, Msg.getString("Command.566"));
		add(ppmTable, 835, 
				57866, Msg.getString("Command.567"));
		add(ppmTable, 836, 
				57867, Msg.getString("Command.568"));
		add(ppmTable, 837, 
				57868, Msg.getString("Command.569"));
		add(ppmTable, 838, 
				57869, Msg.getString("Command.570"));
		add(ppmTable, 839, 
				57870, Msg.getString("Command.571"));
		add(ppmTable, 840, 
				57871, Msg.getString("Command.572"));
		add(ppmTable, 841, 
				57872, Msg.getString("Command.573"));

		add(scmgrtTable, 842, 
				41216, Msg.getString("Command.574"));
		add(scmgrtTable, 862, 
				57857, Msg.getString("Command.593"));
		add(scmgrtTable, 863, 
				57905, Msg.getString("Command.594"));
		add(scmgrtTable, 864, 
				57873, Msg.getString("Command.595"));
		add(scmgrtTable, 865, 
				57921, Msg.getString("Command.596"));
		add(scmgrtTable, 866, 
				57889, Msg.getString("Command.597"));

		add(scmgrcTable, 842, 
				41216, Msg.getString("Command.574"));
		add(scmgrcTable, 872, 
				57857, Msg.getString("Command.598"));
		add(scmgrcTable, 873, 
				57905, Msg.getString("Command.599"));
		add(scmgrcTable, 867, 
				57858, Msg.getString("Command.600"));
		add(scmgrcTable, 868, 
				57906, Msg.getString("Command.601"));
		add(scmgrcTable, 874, 
				57873, Msg.getString("Command.602"));
		add(scmgrcTable, 875, 
				57921, Msg.getString("Command.603"));
		add(scmgrcTable, 869, 
				57874, Msg.getString("Command.604"));
		add(scmgrcTable, 870, 
				57922, Msg.getString("Command.605"));
		add(scmgrcTable, 876, 
				57889, Msg.getString("Command.606"));
		add(scmgrcTable, 871, 
				57890, Msg.getString("Command.607"));

		add(shkTable, 50, 50, Msg.getString("Command.575"));
	}

	private static void add(Map<Integer, Data> table, int command, int commandCode, String descr)
	{
		Data data = new Data();
		data.command = command;
		data.code = commandCode;
		data.descr = descr;
		table.put(Integer.valueOf(command), data);
	}

	public Command(SerialNumber element, byte[] array, boolean is16bitCmd)
	{
		this.ne = element;
		this.command = Functions.b2i(array[0]);
		this.parameters = ((byte[])array.clone());
		setCommandProperties();
		if (is16bitCmd) {
			this.parameters = new byte[array.length + 1];
			System.arraycopy(array, 0, this.parameters, 1, array.length);
			this.parameters[0] = 0;
			this.parameters[1] = (byte)this.commandCode;
		}
		else {
			this.parameters[0] = (byte)this.commandCode;
		}
		this.commandCode = -1;
	}

	public Command(SerialNumber serial, int commandId, byte[] value, boolean is16bitCmd)
	{
		if (value == null) {
			value = new byte[0];
		}

		this.ne = serial;
		this.command = commandId;

		setCommandProperties();

		if (!(is16bitCmd))
		{
			this.parameters = new byte[1 + value.length];
			this.parameters[0] = (byte)this.commandCode;
			System.arraycopy(value, 0, this.parameters, 1, value.length);
		}
		else {
			byte[] commandCodeArray = Functions.l2b(this.commandCode, 2);
			this.parameters = Functions.concatArray(commandCodeArray, value);
		}

		if (value.length != 0)
			this.commandCode = -1;
	}

	public Command(SerialNumber element, int commandId)
	{
		this.ne = element;
		this.command = commandId;
		setCommandProperties();
	}

	public Command(SerialNumber element, int commandId, byte[] param)
	{
		this.ne = element;
		this.command = commandId;

		this.parameters = param;
		setCommandProperties();
	}

	public Command(SerialNumber element, int commandId, byte[] param, int code)
	{
		this.ne = element;
		this.command = commandId;
		this.parameters = param;
		this.commandCode = code;
	}

	public static Map<Integer, Data> getAllCommands()
	{
		Map<Integer, Data> result = new TreeMap<Integer, Data>();
		for (int i = 0; i < 10000; ++i) {
			Map<Integer, Data> table = getCommandTable(i);
			if (table != null) {
				Data value = (Data)table.get(Integer.valueOf(i));
				result.put(Integer.valueOf(i), value);
			}
		}
		return result;
	}

	private static Map<Integer, Data> getCommandTable(int command) {
		Integer commandAsObject = Integer.valueOf(command);
		if (ampTable.containsKey(commandAsObject)) {
			return ampTable;
		}
		if (amp1uTable.containsKey(commandAsObject)) {
			return amp1uTable;
		}
		if (comTable.containsKey(commandAsObject)) {
			return comTable;
		}
		if (fanTable.containsKey(commandAsObject)) {
			return fanTable;
		}
		if (muxTable.containsKey(commandAsObject)) {
			return muxTable;
		}
		if (opsTable.containsKey(commandAsObject)) {
			return opsTable;
		}
		if (supTable.containsKey(commandAsObject)) {
			return supTable;
		}
		if (sw8Table.containsKey(commandAsObject)) {
			return sw8Table;
		}
		if (trpTable.containsKey(commandAsObject)) {
			return trpTable;
		}
		if (trpRegTable.containsKey(commandAsObject)) {
			return trpRegTable;
		}
		if (roadmTable.containsKey(commandAsObject)) {
			return roadmTable;
		}
		if (fanG8Table.containsKey(commandAsObject)) {
			return fanG8Table;
		}
		if (shkTable.containsKey(commandAsObject)) {
			return shkTable;
		}
		if (muxDemuxVoaTable.containsKey(commandAsObject)) {
			return muxDemuxVoaTable;
		}
		if (ppmTable.containsKey(commandAsObject)) {
			return ppmTable;
		}
		if (scmgrtTable.containsKey(commandAsObject)) {
			return scmgrtTable;
		}
		if (scmgrcTable.containsKey(commandAsObject)) {
			return scmgrcTable;
		}
		return null;
	}

	private void setCommandProperties()
	{
		this.commandCode = -2;
		this.name = "Unknown";
		Map<Integer, Data> table = getCommandTable(this.command);
		if (table != null) {
			Data data = (Data)table.get(Integer.valueOf(this.command));
			if (data != null) {
				this.commandCode = data.code;
				this.name = data.descr;
				if ((this.command == 123) || (this.command == 122))
					this.name += this.parameters[0];
			}
		}
	}

	public int getCommandCode()
	{
		return this.commandCode;
	}

	public int getCommand()
	{
		return this.command;
	}

	public SerialNumber getSerialNumber()
	{
		return this.ne;
	}

	public byte[] getParameters()
	{
		return this.parameters;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("Command ");
		buffer.append(this.name);
		buffer.append(" to ");
		buffer.append(this.ne.toShortString());
		return buffer.toString();
	}

	public String getCommandName()
	{
		return this.name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public static void main(String[] args)
	{

		int fieldValue;
		Field existing;
		Field[] fields = Command.class.getDeclaredFields();

		Map<Integer,Field> idMapping = new TreeMap<Integer, Field>();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (fieldName.endsWith("_CODE")) 
				try {
					Object fieldValueObject = field.get(null);
					if (!(fieldValueObject instanceof Integer)) {
						System.out.println("Field " + fieldName + " is of type " + 
								fieldValueObject.getClass().getSimpleName());

					}
					fieldValue = ((Integer)fieldValueObject).intValue();
					existing = (Field)idMapping.put(Integer.valueOf(fieldValue), field);
					if (existing != null)
						System.out.println("ERROR:fields " + existing.getName() + " and " + 
								fieldName + " have the same value.");
				}
			catch (Exception e) {
				System.out.println("Field " + fieldName + " is not accessible.");
			}
		}

		System.out.println();

		System.out.println("ID\tCommand Name");
		for (Iterator<Entry<Integer,Field>> localIterator = idMapping.entrySet().iterator(); localIterator.hasNext(); ) { 
			Entry<Integer,Field> item = localIterator.next();
			System.out.println(item.getKey() + "\t" + ((Field)item.getValue()).getName());
		}
		System.out.println();

		for (Iterator<Entry<Integer,Field>> localIterator = idMapping.entrySet().iterator(); localIterator.hasNext(); ) { 

			Entry<Integer,Field> item  = localIterator.next();
			Field idProperty = (Field)item.getValue();
			int k = 0;

			for (int e = 0; e < fields.length; ++e) { 
				Field field = fields[e];
				String fieldName = field.getName();
				if ((idProperty.getName() + "_CODE").equals(fieldName)) {
					k = 1;
					break;
				}
			}
			if (k == 0)
				System.out.println("Field " + idProperty.getName() + 
				" does not have a match");
		}
	}

	public static class Data
	{
		public int command;
		public int code;
		public String descr;
		
		public String toString(){
			StringBuilder builder = new StringBuilder();
			builder.append("Command ID: ");
			builder.append(command);
			builder.append(", Command Code: ");
			builder.append(code);
			builder.append(", Description: ");
			builder.append(descr);
			return builder.toString();
		}
	}
}