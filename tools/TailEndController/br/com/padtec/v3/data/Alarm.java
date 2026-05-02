package br.com.padtec.v3.data;

import java.awt.Color;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import br.com.padtec.v3.data.NetworkAlarm.ElementType;
import br.com.padtec.v3.util.Msg;

public class Alarm extends Notification {
	
  private static final long serialVersionUID = 14L;
  public static final int CRITICAL = 60;
  public static final int MAJOR = 50;
  public static final int MINOR = 40;
  public static final int WARNING = 30;
  public static final int UNKNOWN = 20;
  public static final int CLEARED = 10;
  public static final int TYPE_ALL = -1;
  public static final int TYPE_UNKNOWN = 0;
  public static final int TYPE_LOS = 1;
  public static final int TYPE_N3DB = 2;
  public static final int TYPE_FAIL = 3;
  public static final int TYPE_LASEROFF = 4;
  public static final int TYPE_LOS2 = 5;
  public static final int TYPE_LASEROFF2 = 6;
  public static final int TYPE_TAXA = 7;
  public static final int TYPE_LOF = 8;
  public static final int TYPE_LOF2 = 9;
  public static final int TYPE_FAIL2 = 10;
  public static final int TYPE_UNLOCK = 11;
  public static final int TYPE_LCT = 12;
  public static final int TYPE_CS_LOS1 = 13;
  public static final int TYPE_CS_LOS2 = 14;
  public static final int TYPE_COMMAND_RX = 15;
  public static final int TYPE_RESTART = 16;
  public static final int TYPE_THRES_PIN = 17;
  public static final int TYPE_THRES_POUT = 18;
  public static final int TYPE_THRES_LAMBDA = 19;
  public static final int TYPE_THRES_TAXA = 20;
  public static final int TYPE_AMP_LOS = 21;
  public static final int TYPE_AMP_FAIL = 22;
  public static final int TYPE_AMP_LASEROFF = 23;
  public static final int TYPE_AMP_ALS = 24;
  public static final int TYPE_AMP_MCS_TEMPERATURE = 25;
  public static final int TYPE_CURRENT_ALARM = 26;
  public static final int TYPE_TEMPERATURE_ALARM = 27;
  public static final int TYPE_AMP_USED_BACKUP_LASER = 28;
  public static final int TYPE_THRES_POT_BOMBEIO = 29;
  public static final int TYPE_OP_COMMUTE = 33;
  public static final int TYPE_OP_AUTOOFF = 34;
  public static final int TYPE_OP_BLOCK = 35;
  public static final int TYPE_OP_LOS1 = 36;
  public static final int TYPE_OP_LOS2 = 37;
  public static final int TYPE_OP_RESERVES = 38;
  public static final int TYPE_SLAVE_DOWN = 49;
  public static final int TYPE_NE_NOT_RESPONDING = 50;
  public static final int TYPE_IP_NOT_RESPONDING = 51;
  public static final int TYPE_NEWIP_NOT_RESPONDING = 52;
  public static final int TYPE_IP_RESPONDING = 53;
  public static final int TYPE_SWICHING_SERVER = 54;
  public static final int TYPE_SLOT_CHANGED = 55;
  public static final int TYPE_CMD_COLECTOR_RX = 56;
  public static final int TYPE_NEW_NE = 57;
  public static final int TYPE_DISK_SPACE = 58;
  public static final int TYPE_GENERIC_SHK = 59;
  public static final int TYPE_MCO_TX = 60;
  public static final int TYPE_MCO_FX = 61;
  public static final int TYPE_SW8_CH_LOST = 62;
  public static final int TYPE_SW8_MOD_FAIL = 63;
  public static final int TYPE_SW8_MOD_DISABLE = 64;
  public static final int TYPE_SW8_MOD_SWITCH = 65;
  public static final int TYPE_TRP_ODU_TIM = 66;
  public static final int TYPE_TRP_ODU_BDI = 67;
  public static final int TYPE_TRP_ODU_AIS = 68;
  public static final int TYPE_TRP_LOS_SYNC = 69;
  public static final int TYPE_TRP_LOS2_SYNC = 70;
  public static final int TYPE_TRP_J0 = 71;
  public static final int TYPE_TRP_ODU_SAPI_DAPI = 72;
  public static final int TYPE_TRP_OTU_SAPI_DAPI = 73;
  public static final int TYPE_TRP_PT = 74;
  public static final int TYPE_TRP_ODU_BIP8 = 75;
  public static final int TYPE_TRP_B1 = 76;
  public static final int TYPE_TRP_OTU_TIM = 77;
  public static final int TYPE_TRP_OTU_BDI = 78;
  public static final int TYPE_TRP_LOM = 79;
  public static final int TYPE_TRP_STAT = 80;
  public static final int TYPE_TRP_OTU_BIP8 = 81;
  public static final int TYPE_TRP_ENCAISOFF = 82;
  public static final int TYPE_TRP_ODU_TTI_REF_TX = 83;
  public static final int TYPE_TRP_ODU_TTI_REF_RX = 84;
  public static final int TYPE_TRP_OTU_TTI_REF_TX = 85;
  public static final int TYPE_TRP_OTU_TTI_REF_RX = 86;
  public static final int TYPE_TRP_OTU_TTI_REF_TX2 = 87;
  public static final int TYPE_TRP_OTU_TTI_REF_RX2 = 88;
  public static final int TYPE_TRP_OTU_TIM2 = 89;
  public static final int TYPE_TRP_OTU_BDI2 = 90;
  public static final int TYPE_TRP_LOM2 = 91;
  public static final int TYPE_TRP_OTU_SAPI_DAPI2 = 92;
  public static final int TYPE_TRP_OTU_BIP8_II = 93;
  public static final int TYPE_TRP_ODU_BEI = 94;
  public static final int TYPE_TRP_OTU_BEI = 95;
  public static final int TYPE_TRP_OTU_BEI2 = 96;
  public static final int TYPE_TRP_FEC_ERROR = 97;
  public static final int TYPE_TRP_FEC_II_ERROR = 98;
  public static final int TYPE_TRP_PLM = 99;
  public static final int TYPE_GENERIC_ROADM = 200;
  public static final int TYPE_FAN_OVERHEAT = 1000;
  public static final int TYPE_FAN_FAIL1 = 1001;
  public static final int TYPE_FAN_FAIL2 = 1002;
  public static final int TYPE_FAN_FAIL3 = 1003;
  public static final int TYPE_PST_OUTRANGE = 1010;
  public static final int TYPE_PST_FAIL_A = 1011;
  public static final int TYPE_PSA_FAIL_A = 1020;
  public static final int TYPE_PSA_FAIL_B = 1021;
  public static final int TYPE_5VDC_AMPGENERAL = 1086;
  public static final int TYPE_TRP_ODU_OP_SPEC = 1030;
  public static final int TYPE_TRP_OTU_OP_SPEC = 1031;
  public static final int TYPE_TRP_ODU_OP_SPEC_REF_TX = 1032;
  public static final int TYPE_TRP_OTU_OP_SPEC_REF_TX = 1033;
  public static final int TYPE_FANG8_OVERHEAT1 = 1034;
  public static final int TYPE_FANG8_OVERHEAT2 = 1035;
  public static final int TYPE_FANG8_FAIL1 = 1036;
  public static final int TYPE_FANG8_FAIL2 = 1037;
  public static final int TYPE_FANG8_FAIL3 = 1038;
  public static final int TYPE_FANG8_FAIL4 = 1039;
  public static final int TYPE_FANG8_FAIL5 = 1040;
  public static final int TYPE_FANG8_FAIL6 = 1041;
  public static final int TYPE_FANG8_FAIL7 = 1042;
  public static final int TYPE_FANG8_FAIL8 = 1043;
  public static final int TYPE_MUXDEMUXGRNoVoa_OVERHEAT = 1044;
  public static final int TYPE_MUXDEMUXGRNoVoa_NOT_READY = 1045;
  public static final int TYPE_MUXDEMUXGRNoVoa_AWG_FAIL = 1046;
  public static final int TYPE_MUXDEMUXVOA_OVERHEAT = 1047;
  public static final int TYPE_MUXDEMUXVOA_NOT_READY = 1048;
  public static final int TYPE_MUXDEMUXVOA_AWG_FAIL = 1049;
  public static final int TYPE_MUX_LOS_SDH1 = 1050;
  public static final int TYPE_MUX_LOF_SDH1 = 1051;
  public static final int TYPE_MUX_FAIL_SDH1 = 1052;
  public static final int TYPE_MUX_LASEROFF_SDH1 = 1053;
  public static final int TYPE_MUX_LOS_SDH2 = 1054;
  public static final int TYPE_MUX_LOF_SDH2 = 1055;
  public static final int TYPE_MUX_FAIL_SDH2 = 1056;
  public static final int TYPE_MUX_LASEROFF_SDH2 = 1057;
  public static final int TYPE_MUX_LOS_SDH3 = 1058;
  public static final int TYPE_MUX_LOF_SDH3 = 1059;
  public static final int TYPE_MUX_FAIL_SDH3 = 1060;
  public static final int TYPE_MUX_LASEROFF_SDH3 = 1061;
  public static final int TYPE_MUX_LOS_SDH4 = 1062;
  public static final int TYPE_MUX_LOF_SDH4 = 1063;
  public static final int TYPE_MUX_FAIL_SDH4 = 1064;
  public static final int TYPE_MUX_LASEROFF_SDH4 = 1065;
  public static final int TYPE_MUX_LOS_SYNC_SDH1 = 1066;
  public static final int TYPE_MUX_LOS_SYNC_SDH2 = 1067;
  public static final int TYPE_MUX_LOS_SYNC_SDH3 = 1068;
  public static final int TYPE_MUX_LOS_SYNC_SDH4 = 1069;
  public static final int TYPE_MUX_J0_SDH1 = 1070;
  public static final int TYPE_MUX_J0_SDH2 = 1071;
  public static final int TYPE_MUX_J0_SDH3 = 1072;
  public static final int TYPE_MUX_J0_SDH4 = 1073;
  public static final int TYPE_MUX_REMOVED_SFP1 = 1074;
  public static final int TYPE_MUX_CHANGED_SFP1 = 1075;
  public static final int TYPE_MUX_REMOVED_SFP2 = 1076;
  public static final int TYPE_MUX_CHANGED_SFP2 = 1077;
  public static final int TYPE_MUX_REMOVED_SFP3 = 1078;
  public static final int TYPE_MUX_CHANGED_SFP3 = 1079;
  public static final int TYPE_MUX_REMOVED_SFP4 = 1080;
  public static final int TYPE_MUX_CHANGED_SFP4 = 1081;
  public static final int TYPE_MUX_B1_SDH1 = 1082;
  public static final int TYPE_MUX_B1_SDH2 = 1083;
  public static final int TYPE_MUX_B1_SDH3 = 1084;
  public static final int TYPE_MUX_B1_SDH4 = 1085;
  public static final int TYPE_THRES_PIN2 = 1100;
  public static final int TYPE_THRES_POUT2 = 1101;
  public static final int TYPE_THRES_LAMBDA2 = 1102;
  public static final int TYPE_THRES_B1 = 1103;
  public static final int TYPE_THRES_BIP8_ODU = 1104;
  public static final int TYPE_THRES_COMMON_PIN = 1105;
  public static final int TYPE_THRES_COMMON_POUT = 1106;
  public static final int TYPE_THRES_EXPRESS_PIN = 1107;
  public static final int TYPE_THRES_EXPRESS_POUT = 1108;
  public static final int TYPE_MUX_HIGH_TEMPERATURE_SFP1 = 1150;
  public static final int TYPE_MUX_LOW_TEMPERATURE_SFP1 = 1151;
  public static final int TYPE_MUX_HIGH_TENSION_SFP1 = 1152;
  public static final int TYPE_MUX_LOW_TENSION_SFP1 = 1153;
  public static final int TYPE_MUX_HIGH_CURRENT_SFP1 = 1154;
  public static final int TYPE_MUX_LOW_CURRENT_SFP1 = 1155;
  public static final int TYPE_MUX_HIGH_POUT_SFP1 = 1156;
  public static final int TYPE_MUX_LOW_POUT_SFP1 = 1157;
  public static final int TYPE_MUX_HIGH_PIN_SFP1 = 1158;
  public static final int TYPE_MUX_LOW_PIN_SFP1 = 1159;
  public static final int TYPE_MUX_HIGH_TEMPERATURE_SFP2 = 1160;
  public static final int TYPE_MUX_LOW_TEMPERATURE_SFP2 = 1161;
  public static final int TYPE_MUX_HIGH_TENSION_SFP2 = 1162;
  public static final int TYPE_MUX_LOW_TENSION_SFP2 = 1163;
  public static final int TYPE_MUX_HIGH_CURRENT_SFP2 = 1164;
  public static final int TYPE_MUX_LOW_CURRENT_SFP2 = 1165;
  public static final int TYPE_MUX_HIGH_POUT_SFP2 = 1166;
  public static final int TYPE_MUX_LOW_POUT_SFP2 = 1167;
  public static final int TYPE_MUX_HIGH_PIN_SFP2 = 1168;
  public static final int TYPE_MUX_LOW_PIN_SFP2 = 1169;
  public static final int TYPE_MUX_HIGH_TEMPERATURE_SFP3 = 1170;
  public static final int TYPE_MUX_LOW_TEMPERATURE_SFP3 = 1171;
  public static final int TYPE_MUX_HIGH_TENSION_SFP3 = 1172;
  public static final int TYPE_MUX_LOW_TENSION_SFP3 = 1173;
  public static final int TYPE_MUX_HIGH_CURRENT_SFP3 = 1174;
  public static final int TYPE_MUX_LOW_CURRENT_SFP3 = 1175;
  public static final int TYPE_MUX_HIGH_POUT_SFP3 = 1176;
  public static final int TYPE_MUX_LOW_POUT_SFP3 = 1177;
  public static final int TYPE_MUX_HIGH_PIN_SFP3 = 1178;
  public static final int TYPE_MUX_LOW_PIN_SFP3 = 1179;
  public static final int TYPE_MUX_HIGH_TEMPERATURE_SFP4 = 1180;
  public static final int TYPE_MUX_LOW_TEMPERATURE_SFP4 = 1181;
  public static final int TYPE_MUX_HIGH_TENSION_SFP4 = 1182;
  public static final int TYPE_MUX_LOW_TENSION_SFP4 = 1183;
  public static final int TYPE_MUX_HIGH_CURRENT_SFP4 = 1184;
  public static final int TYPE_MUX_LOW_CURRENT_SFP4 = 1185;
  public static final int TYPE_MUX_HIGH_POUT_SFP4 = 1186;
  public static final int TYPE_MUX_LOW_POUT_SFP4 = 1187;
  public static final int TYPE_MUX_HIGH_PIN_SFP4 = 1188;
  public static final int TYPE_MUX_LOW_PIN_SFP4 = 1189;
  public static final int TYPE_CURRENT_ALARM1 = 1200;
  public static final int TYPE_CURRENT_ALARM2 = 1201;
  public static final int TYPE_CURRENT_ALARM3 = 1202;
  public static final int TYPE_CURRENT_ALARM4 = 1203;
  public static final int TYPE_TEMPERATURE_ALARM1 = 1204;
  public static final int TYPE_TEMPERATURE_ALARM2 = 1205;
  public static final int TYPE_TEMPERATURE_ALARM3 = 1206;
  public static final int TYPE_TEMPERATURE_ALARM4 = 1207;
  public static final int TYPE_AMP_FAIL1 = 1208;
  public static final int TYPE_AMP_FAIL2 = 1209;
  public static final int TYPE_AMP_FAIL3 = 1210;
  public static final int TYPE_AMP_FAIL4 = 1211;
  public static final int TYPE_CURRENT_AGC_ALARM1 = 1212;
  public static final int TYPE_CURRENT_AGC_ALARM2 = 1213;
  public static final int TYPE_CURRENT_AGC_ALARM3 = 1214;
  public static final int TYPE_CURRENT_AGC_ALARM4 = 1215;
  public static final int TYPE_NE_MOVE = 1300;
  public static final int TYPE_CONN_ADMIN_CLOSE = 1301;
  public static final int TYPE_CMD_NOT_SENT_NE_NOT_RESPONDING = 1302;
  public static final int TYPE_COMB_LOS_SDH = 1350;
  public static final int TYPE_COMB_LOF_SDH = 1351;
  public static final int TYPE_COMB_FAIL_SDH = 1352;
  public static final int TYPE_COMB_LASEROFF_SDH = 1353;
  public static final int TYPE_COMB_LOS_SYNC_SDH = 1354;
  public static final int TYPE_COMB_J0_SDH = 1355;
  public static final int TYPE_COMB_REMOVED_SFP = 1356;
  public static final int TYPE_COMB_B1_SDH = 1358;
  public static final int TYPE_COMB_HIGH_TEMPERATURE_SFP = 1359;
  public static final int TYPE_COMB_LOW_TEMPERATURE_SFP = 1360;
  public static final int TYPE_COMB_HIGH_TENSION_SFP = 1361;
  public static final int TYPE_COMB_LOW_TENSION_SFP = 1362;
  public static final int TYPE_COMB_HIGH_CURRENT_SFP = 1363;
  public static final int TYPE_COMB_LOW_CURRENT_SFP = 1364;
  public static final int TYPE_COMB_HIGH_POUT_SFP = 1365;
  public static final int TYPE_COMB_LOW_POUT_SFP = 1366;
  public static final int TYPE_COMB_HIGH_PIN_SFP = 1367;
  public static final int TYPE_COMB_LOW_PIN_SFP = 1368;
  public static final int TYPE_COMB_CONFIG_CHANGED = 1369;
  public static final int TYPE_COMB_CHANGED_SFP = 1370;
  public static final int TYPE_COMB_OVER_TEMPERATURE = 1371;
  public static final int TYPE_TRP_LASEROFF = 1400;
  public static final int TYPE_LOS1 = 1401;
  public static final int TYPE_LASEROFF1 = 1402;
  public static final int TYPE_TRP_REG_LOS1 = 1403;
  public static final int TYPE_TRP_REG_LOS2 = 1404;
  public static final int TYPE_TRP_REG_LASEROFF1 = 1405;
  public static final int TYPE_TRP_REG_LASEROFF2 = 1406;
  public static final int TYPE_COMB_LOS = 1407;
  public static final int TYPE_COMB_LASEROFF = 1408;
  public static final int TYPE_NE_BACKUP = 1409;
  public static final int TYPE_NE_RESTORE = 1410;
  public static final int TYPE_MUX_LOS = 1411;
  public static final int TYPE_HISTORY_ALARM = 1412;
  public static final int TYPE_HISTORY_ALARM_END = 1413;
  public static final int TYPE_HISTORY_COMMAND = 1414;
  public static final int TYPE_HISTORY_COMMAND_END = 1415;
  public static final int TYPE_HISTORY_PERFORMANCE = 1416;
  public static final int TYPE_HISTORY_PERFORMANCE_END = 1417;
  public static final int TYPE_HISTORY_LAST_ALARM = 1418;
  public static final int TYPE_HISTORY_LAST_ALARM_END = 1419;
  public static final int TYPE_STARTED = 1420;
  public static final int TYPE_MUX_LOF = 1421;
  public static final int TYPE_TRP_NEW_CHANNEL1 = 1422;
  public static final int TYPE_TRP_NEW_CHANNEL2 = 1423;
  public static final int TYPE_TAXA2 = 1430;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW = 1450;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW = 1451;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW = 1452;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW = 1453;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW = 1454;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW = 1455;
  public static final int TYPE_DEGRADED_CHANNEL = 1456;
  public static final int TYPE_ROAM_EXPRESS_INPUT_POWER_FAIL_LOW = 1457;
  public static final int TYPE_ROAM_EXPRESS_OUTPUT_POWER_FAIL_LOW = 1458;
  public static final int TYPE_ROAM_COMMOM_INPUT_POWER_FAIL_LOW = 1459;
  public static final int TYPE_ROAM_COMMOM_OUTPUT_POWER_FAIL_LOW = 1460;
  public static final int TYPE_ROAM_DROP_OUTPUT_POWER_FAIL_LOW = 1461;
  public static final int TYPE_DEMUX_COMMOM_INPUT_POWER_FAIL_LOW = 1462;
  public static final int TYPE_ROAM_MUX_AWG_TEMPERATURE_FAIL_HIGH = 1464;
  public static final int TYPE_ROAM_DEMUX_AWG_TEMPERATURE_FAIL_HIGH = 1465;
  public static final int TYPE_DEMUX_AWG_TEMPERATURE_FAIL_HIGH = 1466;
  public static final int TYPE_DEMUX_VOA_ATTENUATION_FAIL_LOW = 1467;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF = 1468;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE = 1469;
  public static final int TYPE_NE_MOVED = 1470;
  public static final int TYPE_ROADM_SWITCHING_FAIL = 1471;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE = 1472;
  public static final int TYPE_DEMUX_CALIBRATE_OFF = 1473;
  public static final int TYPE_ROADM_OPEN_LOOP = 1474;
  public static final int TYPE_ROADM_ROAM_NOT_CALIBRATED = 1475;
  public static final int TYPE_ROADM_ROAM_IN_CALIBRATION = 1476;
  public static final int TYPE_ROADM_ROAM_CALIBRATED = 1477;
  public static final int TYPE_ROAM_ROADM_5329_RESTART = 1478;
  public static final int TYPE_ROAM_ROADM_UC_RESTART = 1479;
  public static final int TYPE_TRP_B1_SDH1 = 1480;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_1 = 1481;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_2 = 1482;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_3 = 1483;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_4 = 1484;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_5 = 1485;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_6 = 1486;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_7 = 1487;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_8 = 1488;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_9 = 1489;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_10 = 1490;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_11 = 1491;
  public static final int TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_12 = 1492;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_1 = 1493;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_2 = 1494;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_3 = 1495;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_4 = 1496;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_5 = 1497;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_6 = 1498;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_7 = 1499;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_8 = 1500;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_9 = 1501;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_10 = 1502;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_11 = 1503;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_VIA_12 = 1504;
  public static final int TYPE_PPM_FAIL_HIGH_TENSAO_A = 1505;
  public static final int TYPE_PPM_FAIL_HIGH_TENSAO_B = 1506;
  public static final int TYPE_PPM_FAIL_HIGH_TENSAO_TOTAL = 1507;
  public static final int TYPE_PPM_FAIL_LOW_TENSAO_A = 1508;
  public static final int TYPE_PPM_FAIL_LOW_TENSAO_B = 1509;
  public static final int TYPE_PPM_FAIL_LOW_TENSAO_TOTAL = 1510;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_TENSAO_A = 1511;
  public static final int TYPE_PPM_CIRCUITO_ABERTO_TENSAO_B = 1512;
  public static final int TYPE_SCMGRC_LOS_1 = 1513;
  public static final int TYPE_SCMGRC_LOF_1 = 1514;
  public static final int TYPE_SCMGRC_BDI_1 = 1516;
  public static final int TYPE_SCMGRC_LOS_2 = 1517;
  public static final int TYPE_SCMGRC_LOF_2 = 1518;
  public static final int TYPE_SCMGRC_BDI_2 = 1520;
  public static final int TYPE_SCMGRT_LOS_1 = 1521;
  public static final int TYPE_SCMGRT_LOF_1 = 1522;
  public static final int TYPE_SCMGRT_BDI_1 = 1524;
  public static final int TYPE_AMP_ALS_ACTIVE = 1525;
  public static final int TYPE_AMP_MANUALRESTORE_ACTIVE = 1526;
  public static final int TYPE_BLOCK_ON = 1527;
  public static final int TYPE_BLOCK_OFF = 1528;
  public static final int TYPE_BLOCK_USER = 1529;
  public static final int TYPE_TRP_LINK_DOWN = 1530;
  public static final int TYPE_TRP_RATE_CHANGED = 1531;
  public static final int TYPE_TRP_MAX_FRAME_SIZE_CHANGED = 1532;
  public static final int TYPE_TRP_MAC_RECEIVE_LOCAL_FAULT = 1533;
  public static final int TYPE_TRP_MAC_RECEIVE_REMOTE_FAULT = 1534;
  public static final int TYPE_TRP_J0_RX = 1535;
  public static final int TYPE_TRP_J0_TX = 1536;
  public static final int TYPE_TRP_RS_TIM = 1537;
  public static final int TYPE_TRP_CLIENT_SD = 1538;
  public static final int TYPE_TRP_CLIENT_SF = 1539;
  public static final int TYPE_TRP_ENC_RS_TIM = 1540;
  public static final int TYPE_TRP_J0_MODE_FINISHED = 1541;
  public static final int TYPE_DISCOVERY_DISABLED = 1542;
  public static final int TYPE_COMB_LOF = 1543;
  public static final int TYPE_FANG8_OFF = 1544;
  public static final int TYPE_COMB_FAIL_SDH1 = 1550;
  public static final int TYPE_COMB_FAIL_SDH2 = 1551;
  public static final int TYPE_COMB_FAIL_SDH3 = 1552;
  public static final int TYPE_COMB_FAIL_SDH4 = 1553;
  public static final int TYPE_COMB_FAIL_SDH5 = 1554;
  public static final int TYPE_COMB_FAIL_SDH6 = 1555;
  public static final int TYPE_COMB_FAIL_SDH7 = 1556;
  public static final int TYPE_COMB_FAIL_SDH8 = 1557;
  public static final int TYPE_COMB_LASEROFF_SDH1 = 1558;
  public static final int TYPE_COMB_LASEROFF_SDH2 = 1559;
  public static final int TYPE_COMB_LASEROFF_SDH3 = 1560;
  public static final int TYPE_COMB_LASEROFF_SDH4 = 1561;
  public static final int TYPE_COMB_LASEROFF_SDH5 = 1562;
  public static final int TYPE_COMB_LASEROFF_SDH6 = 1563;
  public static final int TYPE_COMB_LASEROFF_SDH7 = 1564;
  public static final int TYPE_COMB_LASEROFF_SDH8 = 1565;
  public static final int TYPE_COMB_LOS_SDH1 = 1566;
  public static final int TYPE_COMB_LOS_SDH2 = 1567;
  public static final int TYPE_COMB_LOS_SDH3 = 1568;
  public static final int TYPE_COMB_LOS_SDH4 = 1569;
  public static final int TYPE_COMB_LOS_SDH5 = 1570;
  public static final int TYPE_COMB_LOS_SDH6 = 1571;
  public static final int TYPE_COMB_LOS_SDH7 = 1572;
  public static final int TYPE_COMB_LOS_SDH8 = 1573;
  public static final int TYPE_COMB_LOF_SDH1 = 1574;
  public static final int TYPE_COMB_LOF_SDH2 = 1575;
  public static final int TYPE_COMB_LOF_SDH3 = 1576;
  public static final int TYPE_COMB_LOF_SDH4 = 1577;
  public static final int TYPE_COMB_LOF_SDH5 = 1578;
  public static final int TYPE_COMB_LOF_SDH6 = 1579;
  public static final int TYPE_COMB_LOF_SDH7 = 1580;
  public static final int TYPE_COMB_LOF_SDH8 = 1581;
  public static final int TYPE_COMB_LOS_SYNC_SDH1 = 1582;
  public static final int TYPE_COMB_LOS_SYNC_SDH2 = 1583;
  public static final int TYPE_COMB_LOS_SYNC_SDH3 = 1584;
  public static final int TYPE_COMB_LOS_SYNC_SDH4 = 1585;
  public static final int TYPE_COMB_LOS_SYNC_SDH5 = 1586;
  public static final int TYPE_COMB_LOS_SYNC_SDH6 = 1587;
  public static final int TYPE_COMB_LOS_SYNC_SDH7 = 1588;
  public static final int TYPE_COMB_LOS_SYNC_SDH8 = 1589;
  public static final int TYPE_COMB_CHANGED_SFP1 = 1590;
  public static final int TYPE_COMB_CHANGED_SFP2 = 1591;
  public static final int TYPE_COMB_CHANGED_SFP3 = 1592;
  public static final int TYPE_COMB_CHANGED_SFP4 = 1593;
  public static final int TYPE_COMB_CHANGED_SFP5 = 1594;
  public static final int TYPE_COMB_CHANGED_SFP6 = 1595;
  public static final int TYPE_COMB_CHANGED_SFP7 = 1596;
  public static final int TYPE_COMB_CHANGED_SFP8 = 1597;
  public static final int TYPE_COMB_REMOVED_SFP1 = 1598;
  public static final int TYPE_COMB_REMOVED_SFP2 = 1599;
  public static final int TYPE_COMB_REMOVED_SFP3 = 1600;
  public static final int TYPE_COMB_REMOVED_SFP4 = 1601;
  public static final int TYPE_COMB_REMOVED_SFP5 = 1602;
  public static final int TYPE_COMB_REMOVED_SFP6 = 1603;
  public static final int TYPE_COMB_REMOVED_SFP7 = 1604;
  public static final int TYPE_COMB_REMOVED_SFP8 = 1605;
  public static final int TYPE_THRES_PIN1 = 1606;
  public static final int TYPE_THRES_PIN3 = 1607;
  public static final int TYPE_THRES_PIN4 = 1608;
  public static final int TYPE_THRES_PIN5 = 1609;
  public static final int TYPE_THRES_PIN6 = 1610;
  public static final int TYPE_THRES_PIN7 = 1611;
  public static final int TYPE_THRES_PIN8 = 1612;
  public static final int TYPE_THRES_POUT1 = 1613;
  public static final int TYPE_THRES_POUT3 = 1614;
  public static final int TYPE_THRES_POUT4 = 1615;
  public static final int TYPE_THRES_POUT5 = 1616;
  public static final int TYPE_THRES_POUT6 = 1617;
  public static final int TYPE_THRES_POUT7 = 1618;
  public static final int TYPE_THRES_POUT8 = 1619;
  public static final int TYPE_TRP_FEC_I_ERROR = 1620;
  public static final int TYPE_LOF1 = 1621;
  public static final int TYPE_TRP_LOM1 = 1622;
  public static final int TYPE_TRP_REBOOT = 1623;
  public static final int TYPE_ODP_CONFIG_ERROR = 1630;
  public static final int TYPE_ODP_CABLE_FAIL = 1631;
  public static final int TYPE_ODP_COMM_LOS = 1632;
  public static final int TYPE_ODP_NEIGHBOUR_ID_CHANGED = 1633;
  public static final int TYPE_ODP_PATH_STATE_CHANGED = 1634;
  public static final int TYPE_ODP_PATH_TYPE_CHANGED = 1635;
  public static final int TYPE_ODP_WAITTORESTORE_ENABLED = 1636;
  public static final int TYPE_ODP_DISABLED = 1637;
  public static final int TYPE_ODP_NEIGHBOUR_CABLE_FAIL = 1638;
  public static final int TYPE_ODP_NEIGHBOUR_COMM_LOS = 1639;
  public static final int TYPE_ODP_LASER_OFF_ODP = 1640;
  public static final int TYPE_ODP_LASER_OFF_ODP_COMB = 1641;
  public static final int TYPE_ODP_MANUAL_LASER_ON = 1642;
  public static final int TYPE_COMB_REBOOT_GFP = 1643;
  public static final int TYPE_TRP_OVER_TEMPERATURE = 1644;
  public static final int TYPE_TRP_ODU_TTI_REF_SAPI_RX = 1645;
  public static final int TYPE_TRP_ODU_TTI_REF_SAPI_TX = 1646;
  public static final int TYPE_TRP_ODU_TTI_REF_DAPI_RX = 1647;
  public static final int TYPE_TRP_ODU_TTI_REF_DAPI_TX = 1648;
  public static final int TYPE_TRP_OTU_TTI_REF_SAPI_RX = 1649;
  public static final int TYPE_TRP_OTU_TTI_REF_SAPI_TX = 1650;
  public static final int TYPE_TRP_OTU_TTI_REF_SAPI_RX2 = 1651;
  public static final int TYPE_TRP_OTU_TTI_REF_SAPI_TX2 = 1652;
  public static final int TYPE_TRP_OTU_TTI_REF_DAPI_RX = 1653;
  public static final int TYPE_TRP_OTU_TTI_REF_DAPI_TX = 1654;
  public static final int TYPE_TRP_OTU_TTI_REF_DAPI_RX2 = 1655;
  public static final int TYPE_TRP_OTU_TTI_REF_DAPI_TX2 = 1656;
  public static final int TYPE_SCMGRT_ALS_ACTING = 1657;
  public static final int TYPE_SCMGRC_ALS_SOUTH_ACTING = 1658;
  public static final int TYPE_SCMGRC_ALS_NORTH_ACTING = 1659;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_1 = 2010;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_2 = 2011;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_3 = 2012;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_4 = 2013;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_5 = 2014;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_6 = 2015;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_7 = 2016;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_8 = 2017;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_9 = 2018;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_10 = 2019;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_11 = 2020;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_12 = 2021;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_13 = 2022;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_14 = 2023;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_15 = 2024;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_16 = 2025;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_17 = 2026;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_18 = 2027;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_19 = 2028;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_20 = 2029;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_21 = 2030;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_22 = 2031;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_23 = 2032;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_24 = 2033;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_25 = 2034;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_26 = 2035;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_27 = 2036;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_28 = 2037;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_29 = 2038;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_30 = 2039;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_31 = 2040;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_32 = 2041;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_33 = 2042;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_34 = 2043;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_35 = 2044;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_36 = 2045;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_37 = 2046;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_38 = 2047;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_39 = 2048;
  public static final int TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_40 = 2049;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_1 = 2050;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_2 = 2051;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_3 = 2052;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_4 = 2053;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_5 = 2054;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_6 = 2055;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_7 = 2056;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_8 = 2057;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_9 = 2058;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_10 = 2059;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_11 = 2060;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_12 = 2061;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_13 = 2062;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_14 = 2063;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_15 = 2064;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_16 = 2065;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_17 = 2066;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_18 = 2067;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_19 = 2068;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_20 = 2069;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_21 = 2070;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_22 = 2071;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_23 = 2072;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_24 = 2073;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_25 = 2074;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_26 = 2075;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_27 = 2076;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_28 = 2077;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_29 = 2078;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_30 = 2079;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_31 = 2080;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_32 = 2081;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_33 = 2082;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_34 = 2083;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_35 = 2084;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_36 = 2085;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_37 = 2086;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_38 = 2087;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_39 = 2088;
  public static final int TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_40 = 2089;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_1 = 2090;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_2 = 2091;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_3 = 2092;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_4 = 2093;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_5 = 2094;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_6 = 2095;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_7 = 2096;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_8 = 2097;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_9 = 2098;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_10 = 2099;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_11 = 2100;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_12 = 2101;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_13 = 2102;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_14 = 2103;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_15 = 2104;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_16 = 2105;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_17 = 2106;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_18 = 2107;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_19 = 2108;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_20 = 2109;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_21 = 2110;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_22 = 2111;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_23 = 2112;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_24 = 2113;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_25 = 2114;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_26 = 2115;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_27 = 2116;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_28 = 2117;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_29 = 2118;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_30 = 2119;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_31 = 2120;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_32 = 2121;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_33 = 2122;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_34 = 2123;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_35 = 2124;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_36 = 2125;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_37 = 2126;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_38 = 2127;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_39 = 2128;
  public static final int TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_40 = 2129;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_1 = 2130;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_2 = 2131;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_3 = 2132;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_4 = 2133;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_5 = 2134;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_6 = 2135;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_7 = 2136;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_8 = 2137;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_9 = 2138;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_10 = 2139;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_11 = 2140;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_12 = 2141;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_13 = 2142;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_14 = 2143;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_15 = 2144;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_16 = 2145;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_17 = 2146;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_18 = 2147;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_19 = 2148;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_20 = 2149;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_21 = 2150;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_22 = 2151;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_23 = 2152;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_24 = 2153;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_25 = 2154;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_26 = 2155;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_27 = 2156;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_28 = 2157;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_29 = 2158;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_30 = 2159;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_31 = 2160;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_32 = 2161;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_33 = 2162;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_34 = 2163;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_35 = 2164;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_36 = 2165;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_37 = 2166;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_38 = 2167;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_39 = 2168;
  public static final int TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_40 = 2169;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_1 = 2170;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_2 = 2171;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_3 = 2172;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_4 = 2173;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_5 = 2174;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_6 = 2175;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_7 = 2176;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_8 = 2177;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_9 = 2178;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_10 = 2179;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_11 = 2180;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_12 = 2181;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_13 = 2182;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_14 = 2183;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_15 = 2184;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_16 = 2185;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_17 = 2186;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_18 = 2187;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_19 = 2188;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_20 = 2189;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_21 = 2190;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_22 = 2191;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_23 = 2192;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_24 = 2193;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_25 = 2194;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_26 = 2195;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_27 = 2196;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_28 = 2197;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_29 = 2198;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_30 = 2199;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_31 = 2200;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_32 = 2201;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_33 = 2202;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_34 = 2203;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_35 = 2204;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_36 = 2205;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_37 = 2206;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_38 = 2207;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_39 = 2208;
  public static final int TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_40 = 2209;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_1 = 2210;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_2 = 2211;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_3 = 2212;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_4 = 2213;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_5 = 2214;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_6 = 2215;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_7 = 2216;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_8 = 2217;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_9 = 2218;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_10 = 2219;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_11 = 2220;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_12 = 2221;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_13 = 2222;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_14 = 2223;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_15 = 2224;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_16 = 2225;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_17 = 2226;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_18 = 2227;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_19 = 2228;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_20 = 2229;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_21 = 2230;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_22 = 2231;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_23 = 2232;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_24 = 2233;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_25 = 2234;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_26 = 2235;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_27 = 2236;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_28 = 2237;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_29 = 2238;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_30 = 2239;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_31 = 2240;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_32 = 2241;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_33 = 2242;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_34 = 2243;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_35 = 2244;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_36 = 2245;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_37 = 2246;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_38 = 2247;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_39 = 2248;
  public static final int TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_40 = 2249;
  public static final int TYPE_DEGRADED_CHANNEL_1 = 2250;
  public static final int TYPE_DEGRADED_CHANNEL_2 = 2251;
  public static final int TYPE_DEGRADED_CHANNEL_3 = 2252;
  public static final int TYPE_DEGRADED_CHANNEL_4 = 2253;
  public static final int TYPE_DEGRADED_CHANNEL_5 = 2254;
  public static final int TYPE_DEGRADED_CHANNEL_6 = 2255;
  public static final int TYPE_DEGRADED_CHANNEL_7 = 2256;
  public static final int TYPE_DEGRADED_CHANNEL_8 = 2257;
  public static final int TYPE_DEGRADED_CHANNEL_9 = 2258;
  public static final int TYPE_DEGRADED_CHANNEL_10 = 2259;
  public static final int TYPE_DEGRADED_CHANNEL_11 = 2260;
  public static final int TYPE_DEGRADED_CHANNEL_12 = 2261;
  public static final int TYPE_DEGRADED_CHANNEL_13 = 2262;
  public static final int TYPE_DEGRADED_CHANNEL_14 = 2263;
  public static final int TYPE_DEGRADED_CHANNEL_15 = 2264;
  public static final int TYPE_DEGRADED_CHANNEL_16 = 2265;
  public static final int TYPE_DEGRADED_CHANNEL_17 = 2266;
  public static final int TYPE_DEGRADED_CHANNEL_18 = 2267;
  public static final int TYPE_DEGRADED_CHANNEL_19 = 2268;
  public static final int TYPE_DEGRADED_CHANNEL_20 = 2269;
  public static final int TYPE_DEGRADED_CHANNEL_21 = 2270;
  public static final int TYPE_DEGRADED_CHANNEL_22 = 2271;
  public static final int TYPE_DEGRADED_CHANNEL_23 = 2272;
  public static final int TYPE_DEGRADED_CHANNEL_24 = 2273;
  public static final int TYPE_DEGRADED_CHANNEL_25 = 2274;
  public static final int TYPE_DEGRADED_CHANNEL_26 = 2275;
  public static final int TYPE_DEGRADED_CHANNEL_27 = 2276;
  public static final int TYPE_DEGRADED_CHANNEL_28 = 2277;
  public static final int TYPE_DEGRADED_CHANNEL_29 = 2278;
  public static final int TYPE_DEGRADED_CHANNEL_30 = 2279;
  public static final int TYPE_DEGRADED_CHANNEL_31 = 2280;
  public static final int TYPE_DEGRADED_CHANNEL_32 = 2281;
  public static final int TYPE_DEGRADED_CHANNEL_33 = 2282;
  public static final int TYPE_DEGRADED_CHANNEL_34 = 2283;
  public static final int TYPE_DEGRADED_CHANNEL_35 = 2284;
  public static final int TYPE_DEGRADED_CHANNEL_36 = 2285;
  public static final int TYPE_DEGRADED_CHANNEL_37 = 2286;
  public static final int TYPE_DEGRADED_CHANNEL_38 = 2287;
  public static final int TYPE_DEGRADED_CHANNEL_39 = 2288;
  public static final int TYPE_DEGRADED_CHANNEL_40 = 2289;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_1 = 2290;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_2 = 2291;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_3 = 2292;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_4 = 2293;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_5 = 2294;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_6 = 2295;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_7 = 2296;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_8 = 2297;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_9 = 2298;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_10 = 2299;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_11 = 2300;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_12 = 2301;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_13 = 2302;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_14 = 2303;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_15 = 2304;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_16 = 2305;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_17 = 2306;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_18 = 2307;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_19 = 2308;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_20 = 2309;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_21 = 2310;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_22 = 2311;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_23 = 2312;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_24 = 2313;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_25 = 2314;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_26 = 2315;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_27 = 2316;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_28 = 2317;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_29 = 2318;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_30 = 2319;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_31 = 2320;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_32 = 2321;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_33 = 2322;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_34 = 2323;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_35 = 2324;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_36 = 2325;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_37 = 2326;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_38 = 2327;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_39 = 2328;
  public static final int TYPE_ROADM_AUTO_SWITCHING_OFF_40 = 2329;
  public static final int TYPE_ROADM_SWITCHING_FAIL_1 = 2330;
  public static final int TYPE_ROADM_SWITCHING_FAIL_2 = 2331;
  public static final int TYPE_ROADM_SWITCHING_FAIL_3 = 2332;
  public static final int TYPE_ROADM_SWITCHING_FAIL_4 = 2333;
  public static final int TYPE_ROADM_SWITCHING_FAIL_5 = 2334;
  public static final int TYPE_ROADM_SWITCHING_FAIL_6 = 2335;
  public static final int TYPE_ROADM_SWITCHING_FAIL_7 = 2336;
  public static final int TYPE_ROADM_SWITCHING_FAIL_8 = 2337;
  public static final int TYPE_ROADM_SWITCHING_FAIL_9 = 2338;
  public static final int TYPE_ROADM_SWITCHING_FAIL_10 = 2339;
  public static final int TYPE_ROADM_SWITCHING_FAIL_11 = 2340;
  public static final int TYPE_ROADM_SWITCHING_FAIL_12 = 2341;
  public static final int TYPE_ROADM_SWITCHING_FAIL_13 = 2342;
  public static final int TYPE_ROADM_SWITCHING_FAIL_14 = 2343;
  public static final int TYPE_ROADM_SWITCHING_FAIL_15 = 2344;
  public static final int TYPE_ROADM_SWITCHING_FAIL_16 = 2345;
  public static final int TYPE_ROADM_SWITCHING_FAIL_17 = 2346;
  public static final int TYPE_ROADM_SWITCHING_FAIL_18 = 2347;
  public static final int TYPE_ROADM_SWITCHING_FAIL_19 = 2348;
  public static final int TYPE_ROADM_SWITCHING_FAIL_20 = 2349;
  public static final int TYPE_ROADM_SWITCHING_FAIL_21 = 2350;
  public static final int TYPE_ROADM_SWITCHING_FAIL_22 = 2351;
  public static final int TYPE_ROADM_SWITCHING_FAIL_23 = 2352;
  public static final int TYPE_ROADM_SWITCHING_FAIL_24 = 2353;
  public static final int TYPE_ROADM_SWITCHING_FAIL_25 = 2354;
  public static final int TYPE_ROADM_SWITCHING_FAIL_26 = 2355;
  public static final int TYPE_ROADM_SWITCHING_FAIL_27 = 2356;
  public static final int TYPE_ROADM_SWITCHING_FAIL_28 = 2357;
  public static final int TYPE_ROADM_SWITCHING_FAIL_29 = 2358;
  public static final int TYPE_ROADM_SWITCHING_FAIL_30 = 2359;
  public static final int TYPE_ROADM_SWITCHING_FAIL_31 = 2360;
  public static final int TYPE_ROADM_SWITCHING_FAIL_32 = 2361;
  public static final int TYPE_ROADM_SWITCHING_FAIL_33 = 2362;
  public static final int TYPE_ROADM_SWITCHING_FAIL_34 = 2363;
  public static final int TYPE_ROADM_SWITCHING_FAIL_35 = 2364;
  public static final int TYPE_ROADM_SWITCHING_FAIL_36 = 2365;
  public static final int TYPE_ROADM_SWITCHING_FAIL_37 = 2366;
  public static final int TYPE_ROADM_SWITCHING_FAIL_38 = 2367;
  public static final int TYPE_ROADM_SWITCHING_FAIL_39 = 2368;
  public static final int TYPE_ROADM_SWITCHING_FAIL_40 = 2369;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_1 = 2370;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_2 = 2371;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_3 = 2372;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_4 = 2373;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_5 = 2374;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_6 = 2375;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_7 = 2376;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_8 = 2377;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_9 = 2378;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_10 = 2379;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_11 = 2380;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_12 = 2381;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_13 = 2382;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_14 = 2383;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_15 = 2384;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_16 = 2385;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_17 = 2386;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_18 = 2387;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_19 = 2388;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_20 = 2389;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_21 = 2390;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_22 = 2391;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_23 = 2392;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_24 = 2393;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_25 = 2394;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_26 = 2395;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_27 = 2396;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_28 = 2397;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_29 = 2398;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_30 = 2399;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_31 = 2400;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_32 = 2401;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_33 = 2402;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_34 = 2403;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_35 = 2404;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_36 = 2405;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_37 = 2406;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_38 = 2407;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_39 = 2408;
  public static final int TYPE_ROADM_SWITCHED_PATH_INACTIVE_40 = 2409;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_1 = 2410;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_2 = 2411;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_3 = 2412;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_4 = 2413;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_5 = 2414;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_6 = 2415;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_7 = 2416;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_8 = 2417;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_9 = 2418;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_10 = 2419;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_11 = 2420;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_12 = 2421;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_13 = 2422;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_14 = 2423;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_15 = 2424;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_16 = 2425;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_17 = 2426;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_18 = 2427;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_19 = 2428;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_20 = 2429;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_21 = 2430;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_22 = 2431;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_23 = 2432;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_24 = 2433;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_25 = 2434;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_26 = 2435;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_27 = 2436;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_28 = 2437;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_29 = 2438;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_30 = 2439;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_31 = 2440;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_32 = 2441;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_33 = 2442;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_34 = 2443;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_35 = 2444;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_36 = 2445;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_37 = 2446;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_38 = 2447;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_39 = 2448;
  public static final int TYPE_DEMUX_CALIBRATE_OFF_40 = 2449;
  public static final int TYPE_ROADM_OPEN_LOOP_1 = 2450;
  public static final int TYPE_ROADM_OPEN_LOOP_2 = 2451;
  public static final int TYPE_ROADM_OPEN_LOOP_3 = 2452;
  public static final int TYPE_ROADM_OPEN_LOOP_4 = 2453;
  public static final int TYPE_ROADM_OPEN_LOOP_5 = 2454;
  public static final int TYPE_ROADM_OPEN_LOOP_6 = 2455;
  public static final int TYPE_ROADM_OPEN_LOOP_7 = 2456;
  public static final int TYPE_ROADM_OPEN_LOOP_8 = 2457;
  public static final int TYPE_ROADM_OPEN_LOOP_9 = 2458;
  public static final int TYPE_ROADM_OPEN_LOOP_10 = 2459;
  public static final int TYPE_ROADM_OPEN_LOOP_11 = 2460;
  public static final int TYPE_ROADM_OPEN_LOOP_12 = 2461;
  public static final int TYPE_ROADM_OPEN_LOOP_13 = 2462;
  public static final int TYPE_ROADM_OPEN_LOOP_14 = 2463;
  public static final int TYPE_ROADM_OPEN_LOOP_15 = 2464;
  public static final int TYPE_ROADM_OPEN_LOOP_16 = 2465;
  public static final int TYPE_ROADM_OPEN_LOOP_17 = 2466;
  public static final int TYPE_ROADM_OPEN_LOOP_18 = 2467;
  public static final int TYPE_ROADM_OPEN_LOOP_19 = 2468;
  public static final int TYPE_ROADM_OPEN_LOOP_20 = 2469;
  public static final int TYPE_ROADM_OPEN_LOOP_21 = 2470;
  public static final int TYPE_ROADM_OPEN_LOOP_22 = 2471;
  public static final int TYPE_ROADM_OPEN_LOOP_23 = 2472;
  public static final int TYPE_ROADM_OPEN_LOOP_24 = 2473;
  public static final int TYPE_ROADM_OPEN_LOOP_25 = 2474;
  public static final int TYPE_ROADM_OPEN_LOOP_26 = 2475;
  public static final int TYPE_ROADM_OPEN_LOOP_27 = 2476;
  public static final int TYPE_ROADM_OPEN_LOOP_28 = 2477;
  public static final int TYPE_ROADM_OPEN_LOOP_29 = 2478;
  public static final int TYPE_ROADM_OPEN_LOOP_30 = 2479;
  public static final int TYPE_ROADM_OPEN_LOOP_31 = 2480;
  public static final int TYPE_ROADM_OPEN_LOOP_32 = 2481;
  public static final int TYPE_ROADM_OPEN_LOOP_33 = 2482;
  public static final int TYPE_ROADM_OPEN_LOOP_34 = 2483;
  public static final int TYPE_ROADM_OPEN_LOOP_35 = 2484;
  public static final int TYPE_ROADM_OPEN_LOOP_36 = 2485;
  public static final int TYPE_ROADM_OPEN_LOOP_37 = 2486;
  public static final int TYPE_ROADM_OPEN_LOOP_38 = 2487;
  public static final int TYPE_ROADM_OPEN_LOOP_39 = 2488;
  public static final int TYPE_ROADM_OPEN_LOOP_40 = 2489;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_1 = 2490;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_2 = 2491;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_3 = 2492;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_4 = 2493;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_5 = 2494;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_6 = 2495;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_7 = 2496;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_8 = 2497;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_9 = 2498;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_10 = 2499;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_11 = 2500;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_12 = 2501;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_13 = 2502;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_14 = 2503;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_15 = 2504;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_16 = 2505;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_17 = 2506;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_18 = 2507;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_19 = 2508;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_20 = 2509;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_21 = 2510;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_22 = 2511;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_23 = 2512;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_24 = 2513;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_25 = 2514;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_26 = 2515;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_27 = 2516;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_28 = 2517;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_29 = 2518;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_30 = 2519;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_31 = 2520;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_32 = 2521;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_33 = 2522;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_34 = 2523;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_35 = 2524;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_36 = 2525;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_37 = 2526;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_38 = 2527;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_39 = 2528;
  public static final int TYPE_ROADM_SWITCHED_PATH_ACTIVE_40 = 2529;
  public static final int TYPE_SHK_START = 10000;
  public static final Color DEFAULT_CRITICAL_COLOR = new Color(255, 0, 0);
  public static final Color DEFAULT_MAJOR_COLOR = new Color(255, 153, 0);
  public static final Color DEFAULT_MINOR_COLOR = new Color(255, 255, 0);
  public static final Color DEFAULT_WARNING_COLOR = new Color(0, 0, 204);
  public static final Color DEFAULT_UNKNOWN_COLOR = Color.WHITE;
  public static final Color DEFAULT_CLEARED_COLOR = new Color(0, 102, 0);
  private String neName;
  private String mapName;
  private String alarmName;
  private String description;
  private boolean ended;
  private boolean email;
  protected boolean partOfMany;
  protected long intermitenceTime;
  private int priority;
  private int alType;
  private long ackDate;
  private long endDate;
  private Integer contact;
  private String ackUser;
  private String ackDescription;
  private NetworkAlarm.Layer networkLayer;
  private static Hashtable<Integer, Color> colors = new Hashtable<Integer, Color>();

  public Alarm(SerialNumber serial, int type, Integer contact)  {
    super(Notification.ID_ALARM, serial);

    this.neName = "";

    this.mapName = "";

    this.alarmName = "";

    this.description = "";

    this.ended = false;

    this.email = false;

    this.partOfMany = false;

    this.intermitenceTime = 0L;

    this.priority = 20;

    this.alType = 0;

    this.contact = -1;

    this.alType = type;
    this.contact = contact;
  }

  public Alarm(SerialNumber serial, int type)
  {
    this(serial, type, new Integer(-1));
  }

  public boolean isAck()
  {
    return (this.ackDate == 0L);
  }

  public String getMapName()
  {
    return this.mapName;
  }

  public void setMapName(String mapa)
  {
    this.mapName = mapa;
  }

  public String getNeName()
  {
    return this.neName;
  }

  public void setNeName(String name)
  {
    this.neName = name;
  }

  public String getAlarmName()
  {
    StringBuffer buffer = new StringBuffer();
    if ((this.contact == null) || (this.contact.intValue() == -1)) {
      return this.alarmName;
    }
    buffer.append(this.alarmName);
    buffer.append(" (");
    buffer.append(this.contact.intValue() + 1);
    buffer.append(")");
    return buffer.toString();
  }

  public void setAlarmName(String ne)
  {
    this.alarmName = ne;
  }

  public int getPriority()
  {
    return this.priority;
  }

  public void setPriority(int p)
  {
    if (p == 10)
      this.ended = true;
    else
      this.priority = p;
  }

  public void setAckDescription(String desc) {
    this.ackDescription = desc;
  }

  public String getAckDescription() {
    return this.ackDescription;
  }

  public void setAckUser(String ackUser) {
    this.ackUser = ackUser;
  }

  public String getAckUser() {
    return this.ackUser;
  }

  public NetworkAlarm.Layer getNetworkLayer()
  {
    return this.networkLayer;
  }

  public void setNetworkLayer(NetworkAlarm.Layer networkLayer)
  {
    this.networkLayer = networkLayer;
  }

  public static Color getPriorityColor(int priority) {
    Color c = (Color)colors.get(Integer.valueOf(priority));
    if (c != null) {
      return c;
    }
    switch (priority)
    {
    case 60:
      colors.put(Integer.valueOf(60), DEFAULT_CRITICAL_COLOR);
      return DEFAULT_CRITICAL_COLOR;
    case 50:
      colors.put(Integer.valueOf(50), DEFAULT_MAJOR_COLOR);
      return DEFAULT_MAJOR_COLOR;
    case 40:
      colors.put(Integer.valueOf(40), DEFAULT_MINOR_COLOR);
      return DEFAULT_MINOR_COLOR;
    case 30:
      colors.put(Integer.valueOf(30), DEFAULT_WARNING_COLOR);
      return DEFAULT_WARNING_COLOR;
    case 20:
      colors.put(Integer.valueOf(20), DEFAULT_UNKNOWN_COLOR);
      return DEFAULT_UNKNOWN_COLOR;
    case 10:
      colors.put(Integer.valueOf(10), DEFAULT_CLEARED_COLOR);
      return DEFAULT_CLEARED_COLOR;
    }
    return Color.WHITE;
  }

  public static Color getBackColor(Alarm a)
  {
    return getBackColor(a.isCleared(), a.isInst(), a.getPriority());
  }

  public static Color getBackColor(NetworkAlarm a) {
    return getBackColor(a.isCleared(), false, a.getAlarmPriority());
  }

  public static Color getBackColor(boolean isCleared, boolean isInst, int priority)
  {
    if ((isCleared) && (!(isInst))) {
      Color c = (Color)colors.get(Integer.valueOf(10));
      if (c == null) {
        c = DEFAULT_CLEARED_COLOR;
        colors.put(Integer.valueOf(10), c);
      }
      return c;
    }
    Color c = (Color)colors.get(Integer.valueOf(priority));
    if (c != null) {
      return c;
    }
    switch (priority)
    {
    case 60:
      colors.put(Integer.valueOf(60), DEFAULT_CRITICAL_COLOR);
      return DEFAULT_CRITICAL_COLOR;
    case 50:
      colors.put(Integer.valueOf(50), DEFAULT_MAJOR_COLOR);
      return DEFAULT_MAJOR_COLOR;
    case 40:
      colors.put(Integer.valueOf(40), DEFAULT_MINOR_COLOR);
      return DEFAULT_MINOR_COLOR;
    case 30:
      colors.put(Integer.valueOf(30), DEFAULT_WARNING_COLOR);
      return DEFAULT_WARNING_COLOR;
    case 20:
      colors.put(Integer.valueOf(20), DEFAULT_UNKNOWN_COLOR);
      return DEFAULT_UNKNOWN_COLOR;
    }
    return Color.WHITE;
  }

  public static Color getForeColor(Alarm a)
  {
    return getForeColor(a.isCleared(), a.isInst(), a.getPriority());
  }

  public static Color getForeColor(NetworkAlarm a) {
    return getForeColor(a.isCleared(), false, a.getAlarmPriority());
  }

  public static Color getForeColor(boolean isCleared, boolean isInst, int priority)
  {
    Color c;
    if ((isCleared) && (!(isInst)))
      c = (Color)colors.get(Integer.valueOf(10));
    else {
      c = (Color)colors.get(Integer.valueOf(priority));
    }
    if (c == null) {
      if ((priority == 60) || ((isCleared) && (!(isInst)))) {
        return Color.WHITE;
      }
      return Color.BLACK;
    }
    int r = c.getRed();
    int g = c.getGreen();
    if ((r > 240) || (g > 240)) {
      return Color.BLACK;
    }
    return Color.WHITE;
  }

  public static Color getPriorityForeColor(int priority) {
    Color c = (Color)colors.get(Integer.valueOf(priority));
    if (c == null) {
      if ((priority == 60) || (priority == 10)) {
        return Color.WHITE;
      }
      return Color.BLACK;
    }
    int r = c.getRed();
    int g = c.getGreen();
    if ((r > 240) || (g > 240)) {
      return Color.BLACK;
    }
    return Color.WHITE;
  }

  public String toExtendedString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(this.priority);
    buffer.append("-");
    buffer.append(this.alarmName);
    if (this.neName.length() != 0) {
      buffer.append("-");
      buffer.append(this.neName);
    }
    if (isAck()) {
      buffer.append(" (v) ");
    }
    if (isCleared()) {
      buffer.append(" ENDED ");
    }
    String result = buffer.toString();
    buffer = null;
    return result;
  }

  public String toString()
  {
    return toExtendedString();
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isEmail()
  {
    return this.email;
  }

  public void setEmail(boolean email)
  {
    this.email = email;
  }

  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Alarm) {
      Alarm a = (Alarm)obj;
      return getKey().equals(a.getKey());
    }
    return false;
  }

  public int hashCode()
  {
    return getKey().intValue();
  }

  public int getAlType()
  {
    return this.alType;
  }

  public boolean isCleared() {
    return this.ended;
  }

  public Date getStartDate()
  {
    return getTimestamp();
  }

  public Date getAckDate()
  {
    if (this.ackDate == 0L) {
      return null;
    }
    return new Date(this.ackDate);
  }

  public void setAckDate(long ackDate)
  {
    this.ackDate = ackDate;
  }

  public Date getEndDate()
  {
    if (this.endDate == 0L) {
      return null;
    }
    return new Date(this.endDate);
  }

  public void setEndDate(long endDate)
  {
    this.endDate = endDate;
    this.ended = true;
  }

  public static int getThresholdType(int type)
  {
    int alarm;
    switch (type)
    {
    case 1:
      alarm = 17;
      break;
    case 2:
      alarm = 18;
      break;
    case 63:
      alarm = 29;
      break;
    case 3:
      alarm = 19;
      break;
    case 4:
      alarm = 20;
      break;
    case 9:
      alarm = 1102;
      break;
    case 287:
      alarm = 1103;
      break;
    case 5:
      alarm = 1104;
      break;
    case 12:
      alarm = 1606;
      break;
    case 13:
      alarm = 1613;
      break;
    case 7:
      alarm = 1100;
      break;
    case 8:
      alarm = 1101;
      break;
    case 14:
      alarm = 1607;
      break;
    case 15:
      alarm = 1614;
      break;
    case 16:
      alarm = 1608;
      break;
    case 17:
      alarm = 1615;
      break;
    case 18:
      alarm = 1609;
      break;
    case 19:
      alarm = 1616;
      break;
    case 20:
      alarm = 1610;
      break;
    case 21:
      alarm = 1617;
      break;
    case 22:
      alarm = 1611;
      break;
    case 23:
      alarm = 1618;
      break;
    case 24:
      alarm = 1612;
      break;
    case 25:
      alarm = 1619;
      break;
    case 61:
      alarm = 1100;
      break;
    case 62:
      alarm = 1101;
      break;
    case 59:
      alarm = 17;
      break;
    case 60:
      alarm = 18;
      break;
    case 55:
      alarm = 1606;
      break;
    case 56:
      alarm = 1613;
      break;
    case 57:
      alarm = 1100;
      break;
    case 58:
      alarm = 1101;
      break;
    case 41:
      alarm = 1105;
      break;
    case 42:
      alarm = 1106;
      break;
    case 43:
      alarm = 1107;
      break;
    case 44:
      alarm = 1108;
      break;
    default:
      alarm = 0;
    }
    return alarm;
  }

  public static Color getTextColor(Alarm a)
  {
    if (a != null) {
      if ((a.isCleared()) && 
        (a.isAck())) {
        return Color.BLACK;
      }

      return getColor(a);
    }
    return Color.BLACK;
  }

  private static Color getColor(Alarm a) {
    Color c = getBackColor(a);
    double r = c.getRed();
    double g = c.getGreen();
    double b = c.getBlue();
    if ((r > 220.0D) || (g > 220.0D) || (b > 220.0D)) {
      r *= 0.7D;
      g *= 0.7D;
      b *= 0.7D;
      return new Color((int)r, (int)g, (int)b);
    }
    return c;
  }

  public boolean isInst()
  {
    boolean inst;
    switch (this.alType)
    {
    case 16:
    case 24:
    case 33:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
    case 71:
    case 72:
    case 73:
    case 74:
    case 80:
    case 83:
    case 84:
    case 85:
    case 86:
    case 87:
    case 88:
    case 92:
    case 1030:
    case 1031:
    case 1032:
    case 1033:
    case 1070:
    case 1071:
    case 1072:
    case 1073:
    case 1075:
    case 1077:
    case 1079:
    case 1081:
    case 1300:
    case 1301:
    case 1302:
    case 1355:
    case 1369:
    case 1370:
    case 1412:
    case 1413:
    case 1414:
    case 1415:
    case 1416:
    case 1417:
    case 1418:
    case 1419:
    case 1420:
    case 1422:
    case 1423:
    case 1470:
    case 1471:
    case 1478:
    case 1479:
    case 1527:
    case 1528:
    case 1531:
    case 1532:
    case 1535:
    case 1536:
    case 1590:
    case 1591:
    case 1592:
    case 1593:
    case 1594:
    case 1595:
    case 1596:
    case 1597:
    case 1623:
    case 1633:
    case 1634:
    case 1635:
    case 1642:
    case 1643:
    case 1645:
    case 1646:
    case 1647:
    case 1648:
    case 1649:
    case 1650:
    case 1651:
    case 1652:
    case 1653:
    case 1654:
    case 1655:
    case 1656:
      inst = true;
      break;
    default:
      inst = false;
    }

    return inst;
  }

  public static Comparator<Alarm> getPriorityComparator()
  {
    return new Comparator<Alarm>() {
      public boolean equals(Object arg0) {
        return super.equals(arg0);
      }

      public int compare(Alarm o1, Alarm o2) {
        if (o1 == o2) {
          return 0;
        }
        if (o1 == null) {
          return 1;
        }
        if (o2 == null) {
          return -1;
        }
        if ((o1.isCleared()) && (!(o1.isInst()))) {
          return 1;
        }
        if ((o2.isCleared()) && (!(o2.isInst()))) {
          return -1;
        }
        return (o2.getPriority() - o1.getPriority());
      }
    };
  }

  public Integer getContact()
  {
    return this.contact;
  }

  public void setContact(Integer contact)
  {
    this.contact = contact;
  }

  public static boolean compareForTypeAndOrigin(Alarm a, Alarm b)
  {
    if ((a.getNeOrigin().getPart() == 23) && (a.getNeOrigin().getSeq() == 1)) {
      return false;
    }
    boolean i = (a.getAlType() == b.getAlType()) && 
      (a.getNeOrigin().equals(b.getNeOrigin())) && 
      (a.getContact().equals(b.getContact()));
    return i;
  }

  public static void setColors(Hashtable<Integer, Color> c)
  {
    colors = c;
  }

  public Type getType()
  {
    switch (this.alType)
    {
    case 1086:
      return Type.DESEMPENHO;
    case 24:
      return Type.FALHA;
    case 22:
      return Type.FALHA;
    case 23:
      return Type.CONFIGURACAO;
    case 1525:
      return Type.FALHA;
    case 1526:
      return Type.FALHA;
    case 21:
      return Type.FALHA;
    case 1358:
      return Type.DESEMPENHO;
    case 56:
    case 1302:
    case 1353:
    case 1355:
    case 1369:
    case 1370:
    case 1408:
    case 1558:
    case 1559:
    case 1560:
    case 1561:
    case 1562:
    case 1563:
    case 1564:
    case 1565:
    case 1590:
    case 1591:
    case 1592:
    case 1593:
    case 1594:
    case 1595:
    case 1596:
    case 1597:
      return Type.CONFIGURACAO;
    case 1350:
    case 1351:
    case 1352:
    case 1354:
    case 1407:
    case 1543:
    case 1550:
    case 1551:
    case 1552:
    case 1553:
    case 1554:
    case 1555:
    case 1556:
    case 1557:
    case 1566:
    case 1567:
    case 1568:
    case 1569:
    case 1570:
    case 1571:
    case 1572:
    case 1573:
    case 1574:
    case 1575:
    case 1576:
    case 1577:
    case 1578:
    case 1579:
    case 1580:
    case 1581:
    case 1582:
    case 1583:
    case 1584:
    case 1585:
    case 1586:
    case 1587:
    case 1588:
    case 1589:
      return Type.FALHA;
    case 1356:
    case 1598:
    case 1599:
    case 1600:
    case 1601:
    case 1602:
    case 1603:
    case 1604:
    case 1605:
      return Type.CONFIGURACAO;
    case 1301:
      return Type.CONFIGURACAO;
    case 13:
      return Type.FALHA;
    case 14:
      return Type.FALHA;
    case 26:
      return Type.FALHA;
    case 58:
      return Type.DESEMPENHO;
    case 3:
      return Type.FALHA;
    case 10:
      return Type.FALHA;
    case 1001:
      return Type.FALHA;
    case 1002:
      return Type.FALHA;
    case 1003:
      return Type.FALHA;
    case 1000:
      return Type.FALHA;
    case 59:
      return Type.HOUSE_KEEPING;
    case 51:
      return Type.FALHA;
    case 53:
      return Type.FALHA;
    case 4:
      return Type.CONFIGURACAO;
    case 1402:
      return Type.CONFIGURACAO;
    case 6:
      return Type.CONFIGURACAO;
    case 12:
      return Type.GERENCIA;
    case 1:
    case 5:
    case 8:
    case 9:
    case 60:
    case 61:
    case 1401:
    case 1621:
      return Type.FALHA;
    case 1082:
      return Type.DESEMPENHO;
    case 1480:
    case 1538:
    case 1539:
      return Type.DESEMPENHO;
    case 1083:
      return Type.DESEMPENHO;
    case 1084:
      return Type.DESEMPENHO;
    case 1085:
      return Type.DESEMPENHO;
    case 1075:
      return Type.CONFIGURACAO;
    case 1077:
      return Type.CONFIGURACAO;
    case 1079:
      return Type.CONFIGURACAO;
    case 1081:
      return Type.CONFIGURACAO;
    case 1052:
      return Type.FALHA;
    case 1056:
      return Type.FALHA;
    case 1060:
      return Type.FALHA;
    case 1064:
      return Type.FALHA;
    case 1158:
      return Type.FALHA;
    case 1168:
      return Type.FALHA;
    case 1178:
      return Type.FALHA;
    case 1188:
      return Type.FALHA;
    case 1156:
      return Type.FALHA;
    case 1166:
      return Type.FALHA;
    case 1176:
      return Type.FALHA;
    case 1186:
      return Type.FALHA;
    case 1070:
      return Type.CONFIGURACAO;
    case 1071:
      return Type.CONFIGURACAO;
    case 1072:
      return Type.CONFIGURACAO;
    case 1073:
      return Type.CONFIGURACAO;
    case 1053:
      return Type.CONFIGURACAO;
    case 1057:
      return Type.CONFIGURACAO;
    case 1061:
      return Type.CONFIGURACAO;
    case 1065:
      return Type.CONFIGURACAO;
    case 1421:
      return Type.FALHA;
    case 1422:
      return Type.CONFIGURACAO;
    case 1423:
      return Type.CONFIGURACAO;
    case 1051:
      return Type.FALHA;
    case 1055:
      return Type.FALHA;
    case 1059:
      return Type.FALHA;
    case 1063:
      return Type.FALHA;
    case 1050:
      return Type.FALHA;
    case 1054:
      return Type.FALHA;
    case 1058:
      return Type.FALHA;
    case 1062:
      return Type.FALHA;
    case 1066:
      return Type.FALHA;
    case 1067:
      return Type.FALHA;
    case 1068:
      return Type.FALHA;
    case 1069:
      return Type.FALHA;
    case 1411:
      return Type.FALHA;
    case 1157:
    case 1159:
    case 1167:
    case 1169:
    case 1177:
    case 1179:
    case 1187:
    case 1189:
      return Type.FALHA;
    case 1200:
    case 1201:
    case 1202:
    case 1203:
    case 1204:
    case 1205:
    case 1206:
    case 1207:
    case 1208:
    case 1209:
    case 1210:
    case 1211:
    case 1212:
    case 1213:
    case 1214:
    case 1215:
      return Type.FALHA;
    case 1074:
      return Type.CONFIGURACAO;
    case 1076:
      return Type.CONFIGURACAO;
    case 1078:
      return Type.CONFIGURACAO;
    case 1080:
      return Type.CONFIGURACAO;
    case 2:
      return Type.FALHA;
    case 1300:
      return Type.CONFIGURACAO;
    case 50:
      return Type.GERENCIA;
    case 57:
      return Type.GERENCIA;
    case 34:
      return Type.PROTECAO;
    case 35:
      return Type.GERENCIA;
    case 33:
      return Type.PROTECAO;
    case 36:
      return Type.FALHA;
    case 37:
      return Type.FALHA;
    case 38:
      return Type.PROTECAO;
    case 1020:
      return Type.FALHA;
    case 1021:
      return Type.FALHA;
    case 1011:
      return Type.FALHA;
    case 1010:
      return Type.FALHA;
    case 16:
      return Type.FALHA;
    case 1420:
      return Type.FALHA;
    case 49:
      return Type.GERENCIA;
    case 55:
      return Type.CONFIGURACAO;
    case 62:
      return Type.PROTECAO;
    case 64:
      return Type.PROTECAO;
    case 63:
      return Type.FALHA;
    case 65:
      return Type.CONFIGURACAO;
    case 54:
      return Type.GERENCIA;
    case 7:
    case 1430:
      return Type.DESEMPENHO;
    case 27:
      return Type.FALHA;
    case 1103:
      return Type.DESEMPENHO;
    case 1104:
      return Type.DESEMPENHO;
    case 19:
      return Type.FALHA;
    case 1102:
      return Type.FALHA;
    case 17:
    case 18:
    case 1100:
    case 1101:
    case 1105:
    case 1106:
    case 1107:
    case 1108:
    case 1606:
    case 1607:
    case 1608:
    case 1609:
    case 1610:
    case 1611:
    case 1612:
    case 1613:
    case 1614:
    case 1615:
    case 1616:
    case 1617:
    case 1618:
    case 1619:
      return Type.DESEMPENHO;
    case 29:
      return Type.DESEMPENHO;
    case 76:
      return Type.DESEMPENHO;
    case 82:
      return Type.CONFIGURACAO;
    case 97:
    case 98:
    case 1620:
      return Type.DESEMPENHO;
    case 67:
    case 68:
    case 69:
    case 70:
    case 79:
    case 91:
    case 1622:
      return Type.FALHA;
    case 94:
      return Type.DESEMPENHO;
    case 75:
      return Type.DESEMPENHO;
    case 1032:
      return Type.CONFIGURACAO;
    case 72:
      return Type.CONFIGURACAO;
    case 66:
      return Type.FALHA;
    case 84:
    case 1645:
    case 1647:
      return Type.CONFIGURACAO;
    case 83:
    case 1646:
    case 1648:
      return Type.CONFIGURACAO;
    case 78:
      return Type.FALHA;
    case 90:
      return Type.FALHA;
    case 95:
      return Type.DESEMPENHO;
    case 96:
      return Type.DESEMPENHO;
    case 93:
      return Type.DESEMPENHO;
    case 81:
      return Type.DESEMPENHO;
    case 1033:
      return Type.CONFIGURACAO;
    case 73:
      return Type.CONFIGURACAO;
    case 92:
      return Type.CONFIGURACAO;
    case 77:
      return Type.FALHA;
    case 89:
      return Type.CONFIGURACAO;
    case 86:
    case 1649:
    case 1653:
      return Type.CONFIGURACAO;
    case 88:
    case 1651:
    case 1655:
      return Type.CONFIGURACAO;
    case 85:
    case 1650:
    case 1654:
      return Type.CONFIGURACAO;
    case 87:
    case 1652:
    case 1656:
      return Type.CONFIGURACAO;
    case 99:
      return Type.FALHA;
    case 74:
      return Type.CONFIGURACAO;
    case 1405:
      return Type.CONFIGURACAO;
    case 1406:
      return Type.CONFIGURACAO;
    case 1403:
      return Type.FALHA;
    case 1404:
      return Type.FALHA;
    case 80:
      return Type.GERENCIA;
    case 0:
      return Type.FALHA;
    case 1412:
    case 1413:
    case 1414:
    case 1415:
    case 1416:
    case 1417:
    case 1418:
    case 1419:
      return Type.GERENCIA;
    case 1034:
    case 1035:
    case 1036:
    case 1037:
    case 1038:
    case 1039:
    case 1040:
    case 1041:
    case 1042:
    case 1043:
    case 1044:
    case 1045:
    case 1046:
    case 1047:
    case 1048:
    case 1049:
    case 1371:
    case 1481:
    case 1482:
    case 1483:
    case 1484:
    case 1485:
    case 1486:
    case 1487:
    case 1488:
    case 1489:
    case 1490:
    case 1491:
    case 1492:
    case 1493:
    case 1494:
    case 1495:
    case 1496:
    case 1497:
    case 1498:
    case 1499:
    case 1500:
    case 1501:
    case 1502:
    case 1503:
    case 1504:
    case 1505:
    case 1506:
    case 1507:
    case 1508:
    case 1509:
    case 1510:
    case 1511:
    case 1512:
    case 1513:
    case 1514:
    case 1516:
    case 1517:
    case 1518:
    case 1520:
    case 1521:
    case 1522:
    case 1524:
    case 1530:
    case 1533:
    case 1534:
    case 1537:
    case 1623:
    case 1631:
    case 1632:
    case 1638:
    case 1639:
    case 1643:
    case 1644:
    case 1657:
    case 1658:
    case 1659:
      return Type.FALHA;
    case 1470:
    case 1527:
    case 1528:
    case 1529:
    case 1531:
    case 1532:
    case 1535:
    case 1536:
    case 1540:
    case 1541:
    case 1542:
    case 1544:
    case 1630:
    case 1633:
    case 1634:
    case 1635:
    case 1636:
    case 1637:
    case 1640:
    case 1641:
    case 1642:
      return Type.CONFIGURACAO;
    }

    return null;
  }

  public static void initiateAlarmConfig(Map<Integer, AlarmConfig> alarmConfig) {
	  alarmConfig.put(15, new AlarmConfig(15, "Alteração de Ganho", false, Alarm.CRITICAL, 
			  "Alteração de ganho no amplificador", 5000));
	  
	  
    alarmConfig.put(2, new AlarmConfig(2, Msg.getString("Alarm.TYPE_N3DB"), 
    		false, 40,  Msg.getString("AlarmDescr.TYPE_N3DB"), 5000L));
    alarmConfig.put(Integer.valueOf(68), 
      new AlarmConfig(68, Msg.getString("Alarm.TYPE_TRP_ODU_AIS"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_ODU_AIS"), 5000L));
    alarmConfig.put(Integer.valueOf(34), 
      new AlarmConfig(34, Msg.getString("Alarm.TYPE_OP_AUTOOFF"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_OP_AUTOOFF"), 5000L));
    alarmConfig.put(Integer.valueOf(59), 
      new AlarmConfig(59, Msg.getString("Alarm.TYPE_GENERIC_SHK"), false, 
      40, Msg.getString("AlarmDescr.TYPE_GENERIC_SHK"), 5000L));
    alarmConfig.put(Integer.valueOf(67), 
      new AlarmConfig(67, Msg.getString("Alarm.TYPE_TRP_ODU_BDI"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_ODU_BDI"), 5000L));
    alarmConfig.put(Integer.valueOf(62), 
      new AlarmConfig(62, Msg.getString("Alarm.TYPE_SW8_CH_LOST"), false, 
      50, Msg.getString("AlarmDescr.TYPE_SW8_CH_LOST"), 5000L));
    alarmConfig.put(Integer.valueOf(35), 
      new AlarmConfig(35, Msg.getString("Alarm.TYPE_OP_BLOCK"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_OP_BLOCK"), 5000L));
    alarmConfig
      .put(Integer.valueOf(56), 
      new AlarmConfig(56, Msg.getString("Alarm.TYPE_CMD_COLECTOR_RX"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_CMD_COLECTOR_RX"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1302), 
      new AlarmConfig(1302, 
      Msg.getString("Alarm.TYPE_CMD_NOT_SENT_NE_NOT_RESPONDING"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_CMD_NOT_SENT_NE_NOT_RESPONDING"), 
      5000L));
    alarmConfig.put(Integer.valueOf(33), 
      new AlarmConfig(33, Msg.getString("Alarm.TYPE_OP_COMMUTE"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_OP_COMMUTE"), 5000L));
    alarmConfig.put(Integer.valueOf(49), 
      new AlarmConfig(49, Msg.getString("Alarm.TYPE_SLAVE_DOWN"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_SLAVE_DOWN"), 5000L));
    alarmConfig.put(Integer.valueOf(54), 
      new AlarmConfig(54, Msg.getString("Alarm.TYPE_SWICHING_SERVER"), 
      false, 40, Msg.getString("AlarmDescr.TYPE_SWICHING_SERVER"), 5000L));
    alarmConfig.put(Integer.valueOf(65), 
      new AlarmConfig(65, Msg.getString("Alarm.TYPE_SW8_MOD_SWITCH"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SW8_MOD_SWITCH"), 5000L));
    alarmConfig.put(Integer.valueOf(0), 
      new AlarmConfig(0, Msg.getString("Alarm.TYPE_UNKNOWN"), false, 20, 
      Msg.getString("AlarmDescr.TYPE_UNKNOWN"), 5000L));
    alarmConfig.put(Integer.valueOf(50), 
      new AlarmConfig(50, Msg.getString("Alarm.TYPE_NE_NOT_RESPONDING"), 
      false, 60, Msg.getString("AlarmDescr.TYPE_NE_NOT_RESPONDING"), 
      5000L));
    alarmConfig.put(Integer.valueOf(22), 
      new AlarmConfig(22, Msg.getString("Alarm.TYPE_AMP_FAIL"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_AMP_FAIL"), 5000L));
    alarmConfig.put(Integer.valueOf(3), 
      new AlarmConfig(3, 
      Msg.getString("Alarm.TYPE_FAIL"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_FAIL"), 5000L));
    alarmConfig.put(Integer.valueOf(63), 
      new AlarmConfig(63, Msg.getString("Alarm.TYPE_SW8_MOD_FAIL"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SW8_MOD_FAIL"), 5000L));
    alarmConfig.put(Integer.valueOf(51), 
      new AlarmConfig(51, Msg.getString("Alarm.TYPE_IP_NOT_RESPONDING"), 
      false, 60, Msg.getString("AlarmDescr.TYPE_IP_NOT_RESPONDING"), 
      5000L));
    alarmConfig.put(Integer.valueOf(58), 
      new AlarmConfig(58, Msg.getString("Alarm.TYPE_DISK_SPACE"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DISK_SPACE"), 5000L));
    alarmConfig.put(Integer.valueOf(38), 
      new AlarmConfig(38, Msg.getString("Alarm.TYPE_OP_RESERVES"), false, 
      40, Msg.getString("AlarmDescr.TYPE_OP_RESERVES"), 5000L));
    alarmConfig.put(Integer.valueOf(12), 
      new AlarmConfig(12, 
      Msg.getString("Alarm.TYPE_LCT"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_LCT"), 5000L));
    alarmConfig.put(Integer.valueOf(52), 
      new AlarmConfig(52, 
      Msg.getString("Alarm.TYPE_NEWIP_NOT_RESPONDING"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_NEWIP_NOT_RESPONDING"), 5000L));
    alarmConfig.put(Integer.valueOf(53), 
      new AlarmConfig(53, Msg.getString("Alarm.TYPE_IP_RESPONDING"), false, 
      30, Msg.getString("AlarmDescr.TYPE_IP_RESPONDING"), 5000L));
    alarmConfig.put(Integer.valueOf(23), 
      new AlarmConfig(23, Msg.getString("Alarm.TYPE_AMP_LASEROFF"), false, 
      50, Msg.getString("AlarmDescr.TYPE_AMP_LASEROFF"), 5000L));
    alarmConfig.put(Integer.valueOf(25), 
      new AlarmConfig(25, 
      Msg.getString("Alarm.TYPE_AMP_MCS_TEMPERATURE"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_AMP_MCS_TEMPERATURE"), 5000L));
    alarmConfig.put(Integer.valueOf(28), 
      new AlarmConfig(28, 
      Msg.getString("Alarm.TYPE_AMP_USED_BACKUP_LASER"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_AMP_USED_BACKUP_LASER"), 5000L));
    alarmConfig.put(Integer.valueOf(26), 
      new AlarmConfig(26, Msg.getString("Alarm.TYPE_CURRENT_ALARM"), false, 
      40, Msg.getString("AlarmDescr.TYPE_CURRENT_ALARM"), 5000L));
    alarmConfig
      .put(Integer.valueOf(27), 
      new AlarmConfig(27, 
      Msg.getString("Alarm.TYPE_TEMPERATURE_ALARM"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TEMPERATURE_ALARM"), 5000L));
    alarmConfig.put(Integer.valueOf(4), 
      new AlarmConfig(4, Msg.getString("Alarm.TYPE_LASEROFF"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_LASEROFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1400), 
      new AlarmConfig(1400, Msg.getString("Alarm.TYPE_TRP_LASEROFF"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_LASEROFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1401), 
      new AlarmConfig(1401, 
      Msg.getString("Alarm.TYPE_LOS1"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_LOS1"), 5000L));

    alarmConfig.put(Integer.valueOf(1402), 
      new AlarmConfig(1402, Msg.getString("Alarm.TYPE_LASEROFF1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_LASEROFF1"), 5000L));

    alarmConfig.put(Integer.valueOf(1403), 
      new AlarmConfig(1403, Msg.getString("Alarm.TYPE_TRP_REG_LOS1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_TRP_REG_LOS1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1405), 
      new AlarmConfig(1405, 
      Msg.getString("Alarm.TYPE_TRP_REG_LASEROFF1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_TRP_REG_LASEROFF1"), 5000L));

    alarmConfig.put(Integer.valueOf(1404), 
      new AlarmConfig(1404, Msg.getString("Alarm.TYPE_TRP_REG_LOS2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_TRP_REG_LOS2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1406), 
      new AlarmConfig(1406, 
      Msg.getString("Alarm.TYPE_TRP_REG_LASEROFF2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_TRP_REG_LASEROFF2"), 5000L));

    alarmConfig.put(Integer.valueOf(6), 
      new AlarmConfig(6, Msg.getString("Alarm.TYPE_LASEROFF2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_LASEROFF2"), 5000L));

    alarmConfig.put(Integer.valueOf(19), 
      new AlarmConfig(19, Msg.getString("Alarm.TYPE_THRES_LAMBDA"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_LAMBDA"), 5000L));

    alarmConfig.put(Integer.valueOf(20), 
      new AlarmConfig(20, Msg.getString("Alarm.TYPE_THRES_TAXA"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_TAXA"), 5000L));

    alarmConfig.put(Integer.valueOf(17), 
      new AlarmConfig(17, Msg.getString("Alarm.TYPE_THRES_PIN"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN"), 5000L));

    alarmConfig.put(Integer.valueOf(18), 
      new AlarmConfig(18, Msg.getString("Alarm.TYPE_THRES_POUT"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_POUT"), 5000L));

    alarmConfig
      .put(Integer.valueOf(29), 
      new AlarmConfig(29, 
      Msg.getString("Alarm.TYPE_THRES_POT_BOMBEIO"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_POT_BOMBEIO"), 5000L));

    alarmConfig.put(Integer.valueOf(8), 
      new AlarmConfig(8, 
      Msg.getString("Alarm.TYPE_LOF"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_LOF"), 5000L));
    alarmConfig.put(Integer.valueOf(1621), 
      new AlarmConfig(1621, 
      Msg.getString("Alarm.TYPE_LOF1"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_LOF1"), 5000L));
    alarmConfig.put(Integer.valueOf(9), 
      new AlarmConfig(9, 
      Msg.getString("Alarm.TYPE_LOF2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_LOF2"), 5000L));
    alarmConfig.put(Integer.valueOf(10), 
      new AlarmConfig(10, 
      Msg.getString("Alarm.TYPE_FAIL2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_FAIL2"), 5000L));
    alarmConfig.put(Integer.valueOf(21), 
      new AlarmConfig(21, Msg.getString("Alarm.TYPE_AMP_LOS"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_AMP_LOS"), 5000L));
    alarmConfig.put(Integer.valueOf(1525), 
      new AlarmConfig(1525, Msg.getString("Alarm.TYPE_AMP_ALS_ACTIVE"), false, 
      40, Msg.getString("AlarmDescr.TYPE_AMP_ALS_ACTIVE"), 5000L));
    alarmConfig.put(Integer.valueOf(1526), 
      new AlarmConfig(1526, 
      Msg.getString("Alarm.TYPE_AMP_MANUALRESTORE_ACTIVE"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_AMP_MANUALRESTORE_ACTIVE"), 5000L));
    alarmConfig.put(Integer.valueOf(36), 
      new AlarmConfig(36, Msg.getString("Alarm.TYPE_OP_LOS1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_OP_LOS1"), 5000L));
    alarmConfig.put(Integer.valueOf(37), 
      new AlarmConfig(37, Msg.getString("Alarm.TYPE_OP_LOS2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_OP_LOS2"), 5000L));
    alarmConfig.put(Integer.valueOf(13), 
      new AlarmConfig(13, Msg.getString("Alarm.TYPE_CS_LOS1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_CS_LOS1"), 5000L));
    alarmConfig.put(Integer.valueOf(14), 
      new AlarmConfig(14, Msg.getString("Alarm.TYPE_CS_LOS2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_CS_LOS2"), 5000L));
    alarmConfig.put(Integer.valueOf(1), 
      new AlarmConfig(1, 
      Msg.getString("Alarm.TYPE_LOS"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_LOS"), 5000L));
    alarmConfig.put(Integer.valueOf(5), 
      new AlarmConfig(5, 
      Msg.getString("Alarm.TYPE_LOS2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_LOS2"), 5000L));
    alarmConfig.put(Integer.valueOf(60), 
      new AlarmConfig(60, 
      Msg.getString("Alarm.TYPE_MCO_TX"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_MCO_TX"), 5000L));
    alarmConfig.put(Integer.valueOf(61), 
      new AlarmConfig(61, 
      Msg.getString("Alarm.TYPE_MCO_FX"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_MCO_FX"), 5000L));
    alarmConfig.put(Integer.valueOf(69), 
      new AlarmConfig(69, Msg.getString("Alarm.TYPE_TRP_LOS_SYNC"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_LOS_SYNC"), 5000L));
    alarmConfig.put(Integer.valueOf(70), 
      new AlarmConfig(70, Msg.getString("Alarm.TYPE_TRP_LOS2_SYNC"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_LOS2_SYNC"), 5000L));
    alarmConfig.put(Integer.valueOf(57), 
      new AlarmConfig(57, 
      Msg.getString("Alarm.TYPE_NEW_NE"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_NEW_NE"), 5000L));
    alarmConfig.put(Integer.valueOf(71), 
      new AlarmConfig(71, 
      Msg.getString("Alarm.TYPE_TRP_J0"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_J0"), 5000L));
    alarmConfig.put(Integer.valueOf(72), 
      new AlarmConfig(72, Msg.getString("Alarm.TYPE_TRP_ODU_SAPI_DAPI"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_TRP_ODU_SAPI_DAPI"), 
      5000L));
    alarmConfig.put(Integer.valueOf(73), 
      new AlarmConfig(73, Msg.getString("Alarm.TYPE_TRP_OTU_SAPI_DAPI"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_TRP_OTU_SAPI_DAPI"), 
      5000L));
    alarmConfig.put(Integer.valueOf(74), 
      new AlarmConfig(74, 
      Msg.getString("Alarm.TYPE_TRP_PT"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_PT"), 5000L));
    alarmConfig.put(Integer.valueOf(64), 
      new AlarmConfig(64, Msg.getString("Alarm.TYPE_SW8_MOD_DISABLE"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_SW8_MOD_DISABLE"), 5000L));
    alarmConfig.put(Integer.valueOf(16), 
      new AlarmConfig(16, Msg.getString("Alarm.TYPE_RESTART"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_RESTART"), 5000L));
    alarmConfig.put(Integer.valueOf(1420), 
      new AlarmConfig(1420, Msg.getString("Alarm.TYPE_STARTED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_STARTED"), 5000L));
    alarmConfig.put(Integer.valueOf(55), 
      new AlarmConfig(55, Msg.getString("Alarm.TYPE_SLOT_CHANGED"), false, 
      30, Msg.getString("AlarmDescr.TYPE_SLOT_CHANGED"), 5000L));
    alarmConfig.put(Integer.valueOf(11), 
      new AlarmConfig(11, 
      Msg.getString("Alarm.TYPE_UNLOCK"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_UNLOCK"), 5000L));
    alarmConfig.put(Integer.valueOf(7), 
      new AlarmConfig(7, 
      Msg.getString("Alarm.TYPE_TAXA"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TAXA"), 5000L));
    alarmConfig.put(Integer.valueOf(1430), 
      new AlarmConfig(1430, 
      Msg.getString("Alarm.TYPE_TAXA2"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TAXA2"), 5000L));
    alarmConfig.put(Integer.valueOf(66), 
      new AlarmConfig(66, Msg.getString("Alarm.TYPE_TRP_ODU_TIM"), false, 
      60, Msg.getString("AlarmDescr.TYPE_TRP_ODU_TIM"), 5000L));
    alarmConfig.put(Integer.valueOf(75), 
      new AlarmConfig(75, Msg.getString("Alarm.TYPE_TRP_ODU_BIP8"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_ODU_BIP8"), 5000L));
    alarmConfig.put(Integer.valueOf(76), 
      new AlarmConfig(76, 
      Msg.getString("Alarm.TYPE_TRP_B1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TRP_B1"), 5000L));
    alarmConfig.put(Integer.valueOf(77), 
      new AlarmConfig(77, Msg.getString("Alarm.TYPE_TRP_OTU_TIM"), false, 
      60, Msg.getString("AlarmDescr.TYPE_TRP_OTU_TIM"), 5000L));
    alarmConfig.put(Integer.valueOf(78), 
      new AlarmConfig(78, Msg.getString("Alarm.TYPE_TRP_OTU_BDI"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_OTU_BDI"), 5000L));
    alarmConfig.put(Integer.valueOf(79), 
      new AlarmConfig(79, Msg.getString("Alarm.TYPE_TRP_LOM"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_TRP_LOM"), 5000L));
    alarmConfig.put(Integer.valueOf(1622), 
      new AlarmConfig(1622, Msg.getString("Alarm.TYPE_TRP_LOM1"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_TRP_LOM1"), 5000L));
    alarmConfig.put(Integer.valueOf(80), 
      new AlarmConfig(80, Msg.getString("Alarm.TYPE_TRP_STAT"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_STAT"), 5000L));
    alarmConfig.put(Integer.valueOf(81), 
      new AlarmConfig(81, Msg.getString("Alarm.TYPE_TRP_OTU_BIP8"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_OTU_BIP8"), 5000L));
    alarmConfig.put(Integer.valueOf(82), 
      new AlarmConfig(82, Msg.getString("Alarm.TYPE_TRP_ENCAISOFF"), false, 
      30, Msg.getString("AlarmDescr.TYPE_TRP_ENCAISOFF"), 5000L));
    alarmConfig.put(Integer.valueOf(83), 
      new AlarmConfig(83, 
      Msg.getString("Alarm.TYPE_TRP_ODU_TTI_REF_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_TTI_REF_TX"), 5000L));
    alarmConfig.put(Integer.valueOf(84), 
      new AlarmConfig(84, 
      Msg.getString("Alarm.TYPE_TRP_ODU_TTI_REF_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_TTI_REF_RX"), 5000L));

    alarmConfig.put(Integer.valueOf(1646), 
      new AlarmConfig(1646, 
      Msg.getString("Alarm.TYPE_TRP_ODU_TTI_REF_SAPI_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_TTI_REF_SAPI_TX"), 5000L));
    alarmConfig.put(Integer.valueOf(1645), 
      new AlarmConfig(1645, 
      Msg.getString("Alarm.TYPE_TRP_ODU_TTI_REF_SAPI_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_TTI_REF_SAPI_RX"), 5000L));

    alarmConfig.put(Integer.valueOf(1648), 
      new AlarmConfig(1648, 
      Msg.getString("Alarm.TYPE_TRP_ODU_TTI_REF_DAPI_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_TTI_REF_DAPI_TX"), 5000L));
    alarmConfig.put(Integer.valueOf(1647), 
      new AlarmConfig(1647, 
      Msg.getString("Alarm.TYPE_TRP_ODU_TTI_REF_DAPI_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_TTI_REF_DAPI_RX"), 5000L));

    alarmConfig.put(Integer.valueOf(85), 
      new AlarmConfig(85, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_TX"), 5000L));

    alarmConfig.put(Integer.valueOf(1650), 
      new AlarmConfig(1650, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_SAPI_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_SAPI_TX"), 5000L));

    alarmConfig.put(Integer.valueOf(1654), 
      new AlarmConfig(1654, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_DAPI_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_DAPI_TX"), 5000L));

    alarmConfig.put(Integer.valueOf(86), 
      new AlarmConfig(86, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_RX"), 5000L));

    alarmConfig.put(Integer.valueOf(1653), 
      new AlarmConfig(1653, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_DAPI_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_DAPI_RX"), 5000L));

    alarmConfig.put(Integer.valueOf(1649), 
      new AlarmConfig(1649, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_SAPI_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_SAPI_RX"), 5000L));

    alarmConfig.put(Integer.valueOf(87), 
      new AlarmConfig(87, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_TX2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_TX2"), 5000L));

    alarmConfig.put(Integer.valueOf(1656), 
      new AlarmConfig(1656, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_DAPI_TX2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_DAPI_TX2"), 5000L));

    alarmConfig.put(Integer.valueOf(1652), 
      new AlarmConfig(1652, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_SAPI_TX2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_SAPI_TX2"), 5000L));

    alarmConfig.put(Integer.valueOf(88), 
      new AlarmConfig(88, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_RX2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_RX2"), 5000L));

    alarmConfig.put(Integer.valueOf(1655), 
      new AlarmConfig(1655, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_DAPI_RX2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_DAPI_RX2"), 5000L));

    alarmConfig.put(Integer.valueOf(1651), 
      new AlarmConfig(1651, 
      Msg.getString("Alarm.TYPE_TRP_OTU_TTI_REF_SAPI_RX2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_TTI_REF_SAPI_RX2"), 5000L));

    alarmConfig.put(Integer.valueOf(89), 
      new AlarmConfig(89, Msg.getString("Alarm.TYPE_TRP_OTU_TIM2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_TRP_OTU_TIM2"), 5000L));
    alarmConfig.put(Integer.valueOf(90), 
      new AlarmConfig(90, Msg.getString("Alarm.TYPE_TRP_OTU_BDI2"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_OTU_BDI2"), 5000L));
    alarmConfig.put(Integer.valueOf(91), 
      new AlarmConfig(91, Msg.getString("Alarm.TYPE_TRP_LOM2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_TRP_LOM2"), 5000L));
    alarmConfig.put(Integer.valueOf(92), 
      new AlarmConfig(92, 
      Msg.getString("Alarm.TYPE_TRP_OTU_SAPI_DAPI2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_SAPI_DAPI2"), 5000L));
    alarmConfig.put(Integer.valueOf(93), 
      new AlarmConfig(93, Msg.getString("Alarm.TYPE_TRP_OTU_BIP8_II"), 
      false, 40, Msg.getString("AlarmDescr.TYPE_TRP_OTU_BIP8_II"), 5000L));
    alarmConfig.put(Integer.valueOf(94), 
      new AlarmConfig(94, Msg.getString("Alarm.TYPE_TRP_ODU_BEI"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_ODU_BEI"), 5000L));
    alarmConfig.put(Integer.valueOf(95), 
      new AlarmConfig(95, Msg.getString("Alarm.TYPE_TRP_OTU_BEI"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_OTU_BEI"), 5000L));
    alarmConfig.put(Integer.valueOf(96), 
      new AlarmConfig(96, Msg.getString("Alarm.TYPE_TRP_OTU_BEI2"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_OTU_BEI2"), 5000L));
    alarmConfig.put(Integer.valueOf(97), 
      new AlarmConfig(97, Msg.getString("Alarm.TYPE_TRP_FEC_ERROR"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_FEC_ERROR"), 5000L));
    alarmConfig.put(Integer.valueOf(1620), 
      new AlarmConfig(1620, Msg.getString("Alarm.TYPE_TRP_FEC_I_ERROR"), 
      false, 40, Msg.getString("AlarmDescr.TYPE_TRP_FEC_I_ERROR"), 5000L));
    alarmConfig.put(Integer.valueOf(98), 
      new AlarmConfig(98, Msg.getString("Alarm.TYPE_TRP_FEC_II_ERROR"), 
      false, 40, Msg.getString("AlarmDescr.TYPE_TRP_FEC_II_ERROR"), 5000L));
    alarmConfig.put(Integer.valueOf(99), 
      new AlarmConfig(99, Msg.getString("Alarm.TYPE_TRP_PLM"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_TRP_PLM"), 5000L));
    alarmConfig.put(Integer.valueOf(1000), 
      new AlarmConfig(1000, Msg.getString("Alarm.TYPE_FAN_OVERHEAT"), false, 
      60, Msg.getString("AlarmDescr.TYPE_FAN_OVERHEAT"), 5000L));
    alarmConfig.put(Integer.valueOf(1001), 
      new AlarmConfig(1001, Msg.getString("Alarm.TYPE_FAN_FAIL1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_FAN_FAIL1"), 5000L));
    alarmConfig.put(Integer.valueOf(1002), 
      new AlarmConfig(1002, Msg.getString("Alarm.TYPE_FAN_FAIL2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_FAN_FAIL2"), 5000L));
    alarmConfig.put(Integer.valueOf(1003), 
      new AlarmConfig(1003, Msg.getString("Alarm.TYPE_FAN_FAIL3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_FAN_FAIL3"), 5000L));
    alarmConfig.put(Integer.valueOf(24), 
      new AlarmConfig(24, Msg.getString("Alarm.TYPE_AMP_ALS"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_AMP_ALS"), 5000L));
    alarmConfig.put(Integer.valueOf(1010), 
      new AlarmConfig(1010, Msg.getString("Alarm.TYPE_PST_OUTRANGE"), false, 
      50, Msg.getString("AlarmDescr.TYPE_PST_OUTRANGE"), 5000L));
    alarmConfig.put(Integer.valueOf(1011), 
      new AlarmConfig(1011, Msg.getString("Alarm.TYPE_PST_FAIL_A"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_PST_FAIL_A"), 5000L));

    alarmConfig.put(Integer.valueOf(1020), 
      new AlarmConfig(1020, Msg.getString("Alarm.TYPE_PSA_FAIL_A"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_PSA_FAIL_A"), 5000L));
    alarmConfig.put(Integer.valueOf(1021), 
      new AlarmConfig(1021, Msg.getString("Alarm.TYPE_PSA_FAIL_B"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_PSA_FAIL_B"), 5000L));
    alarmConfig.put(Integer.valueOf(1086), 
      new AlarmConfig(1086, Msg.getString("Alarm.TYPE_5VDC_AMPGENERAL"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_5VDC_AMPGENERAL"), 5000L));

    alarmConfig.put(Integer.valueOf(1606), 
      new AlarmConfig(1606, Msg.getString("Alarm.TYPE_THRES_PIN1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN1"), 5000L));

    alarmConfig.put(Integer.valueOf(1613), 
      new AlarmConfig(1613, Msg.getString("Alarm.TYPE_THRES_POUT1"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT1"), 5000L));

    alarmConfig.put(Integer.valueOf(1100), 
      new AlarmConfig(1100, Msg.getString("Alarm.TYPE_THRES_PIN2"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN2"), 5000L));

    alarmConfig.put(Integer.valueOf(1101), 
      new AlarmConfig(1101, Msg.getString("Alarm.TYPE_THRES_POUT2"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT2"), 5000L));

    alarmConfig.put(Integer.valueOf(1607), 
      new AlarmConfig(1607, Msg.getString("Alarm.TYPE_THRES_PIN3"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN3"), 5000L));

    alarmConfig.put(Integer.valueOf(1614), 
      new AlarmConfig(1614, Msg.getString("Alarm.TYPE_THRES_POUT3"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT3"), 5000L));

    alarmConfig.put(Integer.valueOf(1608), 
      new AlarmConfig(1608, Msg.getString("Alarm.TYPE_THRES_PIN4"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN4"), 5000L));

    alarmConfig.put(Integer.valueOf(1615), 
      new AlarmConfig(1615, Msg.getString("Alarm.TYPE_THRES_POUT4"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT4"), 5000L));

    alarmConfig.put(Integer.valueOf(1609), 
      new AlarmConfig(1609, Msg.getString("Alarm.TYPE_THRES_PIN5"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN5"), 5000L));

    alarmConfig.put(Integer.valueOf(1616), 
      new AlarmConfig(1616, Msg.getString("Alarm.TYPE_THRES_POUT5"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT5"), 5000L));

    alarmConfig.put(Integer.valueOf(1610), 
      new AlarmConfig(1610, Msg.getString("Alarm.TYPE_THRES_PIN6"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN6"), 5000L));

    alarmConfig.put(Integer.valueOf(1617), 
      new AlarmConfig(1617, Msg.getString("Alarm.TYPE_THRES_POUT6"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT6"), 5000L));

    alarmConfig.put(Integer.valueOf(1611), 
      new AlarmConfig(1611, Msg.getString("Alarm.TYPE_THRES_PIN7"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN7"), 5000L));

    alarmConfig.put(Integer.valueOf(1618), 
      new AlarmConfig(1618, Msg.getString("Alarm.TYPE_THRES_POUT7"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT7"), 5000L));

    alarmConfig.put(Integer.valueOf(1612), 
      new AlarmConfig(1612, Msg.getString("Alarm.TYPE_THRES_PIN8"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_PIN8"), 5000L));

    alarmConfig.put(Integer.valueOf(1619), 
      new AlarmConfig(1619, Msg.getString("Alarm.TYPE_THRES_POUT8"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_POUT8"), 5000L));

    alarmConfig.put(Integer.valueOf(1102), 
      new AlarmConfig(1102, Msg.getString("Alarm.TYPE_THRES_LAMBDA2"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_LAMBDA2"), 5000L));

    alarmConfig.put(Integer.valueOf(1103), 
      new AlarmConfig(1103, Msg.getString("Alarm.TYPE_THRES_B1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_B1"), 5000L));

    alarmConfig.put(Integer.valueOf(1104), 
      new AlarmConfig(1104, Msg.getString("Alarm.TYPE_THRES_BIP8_ODU"), false, 
      40, Msg.getString("AlarmDescr.TYPE_THRES_BIP8_ODU"), 5000L));

    alarmConfig.put(Integer.valueOf(1105), 
      new AlarmConfig(1105, Msg.getString("Alarm.TYPE_THRES_COMMON_PIN"), 
      false, 40, Msg.getString("AlarmDescr.TYPE_THRES_COMMON_PIN"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1106), 
      new AlarmConfig(1106, 
      Msg.getString("Alarm.TYPE_THRES_COMMON_POUT"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_COMMON_POUT"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1107), 
      new AlarmConfig(1107, 
      Msg.getString("Alarm.TYPE_THRES_EXPRESS_PIN"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_EXPRESS_PIN"), 5000L));

    alarmConfig.put(Integer.valueOf(1108), 
      new AlarmConfig(1108, 
      Msg.getString("Alarm.TYPE_THRES_EXPRESS_POUT"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_THRES_EXPRESS_POUT"), 5000L));

    alarmConfig.put(Integer.valueOf(200), 
      new AlarmConfig(200, Msg.getString("Alarm.TYPE_GENERIC_ROADM"), false, 
      50, Msg.getString("AlarmDescr.TYPE_GENERIC_ROADM"), 5000L));

    alarmConfig.put(Integer.valueOf(1538), 
      new AlarmConfig(1538, Msg.getString("Alarm.TYPE_TRP_CLIENT_SD"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_CLIENT_SD"), 5000L));

    alarmConfig.put(Integer.valueOf(1539), 
      new AlarmConfig(1539, Msg.getString("Alarm.TYPE_TRP_CLIENT_SF"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_CLIENT_SF"), 5000L));

    alarmConfig.put(Integer.valueOf(1032), 
      new AlarmConfig(1032, 
      Msg.getString("Alarm.TYPE_TRP_ODU_OP_SPEC_REF_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_ODU_OP_SPEC_REF_TX"), 5000L));

    alarmConfig.put(Integer.valueOf(1033), 
      new AlarmConfig(1033, 
      Msg.getString("Alarm.TYPE_TRP_OTU_OP_SPEC_REF_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_OTU_OP_SPEC_REF_TX"), 5000L));

    alarmConfig.put(Integer.valueOf(1050), 
      new AlarmConfig(1050, Msg.getString("Alarm.TYPE_MUX_LOS_SDH1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOS_SDH1"), 5000L));

    alarmConfig.put(Integer.valueOf(1421), 
      new AlarmConfig(1421, Msg.getString("Alarm.TYPE_MUX_LOF"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOF"), 5000L));

    alarmConfig.put(Integer.valueOf(1051), 
      new AlarmConfig(1051, Msg.getString("Alarm.TYPE_MUX_LOF_SDH1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOF_SDH1"), 5000L));

    alarmConfig.put(Integer.valueOf(1052), 
      new AlarmConfig(1052, Msg.getString("Alarm.TYPE_MUX_FAIL_SDH1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_FAIL_SDH1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1053), 
      new AlarmConfig(1053, 
      Msg.getString("Alarm.TYPE_MUX_LASEROFF_SDH1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LASEROFF_SDH1"), 5000L));

    alarmConfig.put(Integer.valueOf(1054), 
      new AlarmConfig(1054, Msg.getString("Alarm.TYPE_MUX_LOS_SDH2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOS_SDH2"), 5000L));

    alarmConfig.put(Integer.valueOf(1055), 
      new AlarmConfig(1055, Msg.getString("Alarm.TYPE_MUX_LOF_SDH2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOF_SDH2"), 5000L));

    alarmConfig.put(Integer.valueOf(1056), 
      new AlarmConfig(1056, Msg.getString("Alarm.TYPE_MUX_FAIL_SDH2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_FAIL_SDH2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1057), 
      new AlarmConfig(1057, 
      Msg.getString("Alarm.TYPE_MUX_LASEROFF_SDH2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LASEROFF_SDH2"), 5000L));

    alarmConfig.put(Integer.valueOf(1058), 
      new AlarmConfig(1058, Msg.getString("Alarm.TYPE_MUX_LOS_SDH3"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOS_SDH3"), 5000L));

    alarmConfig.put(Integer.valueOf(1059), 
      new AlarmConfig(1059, Msg.getString("Alarm.TYPE_MUX_LOF_SDH3"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOF_SDH3"), 5000L));

    alarmConfig.put(Integer.valueOf(1060), 
      new AlarmConfig(1060, Msg.getString("Alarm.TYPE_MUX_FAIL_SDH3"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_FAIL_SDH3"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1061), 
      new AlarmConfig(1061, 
      Msg.getString("Alarm.TYPE_MUX_LASEROFF_SDH3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LASEROFF_SDH3"), 5000L));

    alarmConfig.put(Integer.valueOf(1062), 
      new AlarmConfig(1062, Msg.getString("Alarm.TYPE_MUX_LOS_SDH4"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOS_SDH4"), 5000L));

    alarmConfig.put(Integer.valueOf(1063), 
      new AlarmConfig(1063, Msg.getString("Alarm.TYPE_MUX_LOF_SDH4"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_LOF_SDH4"), 5000L));

    alarmConfig.put(Integer.valueOf(1064), 
      new AlarmConfig(1064, Msg.getString("Alarm.TYPE_MUX_FAIL_SDH4"), false, 
      60, Msg.getString("AlarmDescr.TYPE_MUX_FAIL_SDH4"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1065), 
      new AlarmConfig(1065, 
      Msg.getString("Alarm.TYPE_MUX_LASEROFF_SDH4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LASEROFF_SDH4"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1066), 
      new AlarmConfig(1066, 
      Msg.getString("Alarm.TYPE_MUX_LOS_SYNC_SDH1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOS_SYNC_SDH1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1067), 
      new AlarmConfig(1067, 
      Msg.getString("Alarm.TYPE_MUX_LOS_SYNC_SDH2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOS_SYNC_SDH2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1068), 
      new AlarmConfig(1068, 
      Msg.getString("Alarm.TYPE_MUX_LOS_SYNC_SDH3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOS_SYNC_SDH3"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1069), 
      new AlarmConfig(1069, 
      Msg.getString("Alarm.TYPE_MUX_LOS_SYNC_SDH4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOS_SYNC_SDH4"), 5000L));

    alarmConfig.put(Integer.valueOf(1070), 
      new AlarmConfig(1070, Msg.getString("Alarm.TYPE_MUX_J0_SDH1"), false, 
      30, Msg.getString("AlarmDescr.TYPE_MUX_J0_SDH1"), 5000L));

    alarmConfig.put(Integer.valueOf(1071), 
      new AlarmConfig(1071, Msg.getString("Alarm.TYPE_MUX_J0_SDH2"), false, 
      30, Msg.getString("AlarmDescr.TYPE_MUX_J0_SDH2"), 5000L));

    alarmConfig.put(Integer.valueOf(1072), 
      new AlarmConfig(1072, Msg.getString("Alarm.TYPE_MUX_J0_SDH3"), false, 
      30, Msg.getString("AlarmDescr.TYPE_MUX_J0_SDH3"), 5000L));

    alarmConfig.put(Integer.valueOf(1073), 
      new AlarmConfig(1073, Msg.getString("Alarm.TYPE_MUX_J0_SDH4"), false, 
      30, Msg.getString("AlarmDescr.TYPE_MUX_J0_SDH4"), 5000L));

    alarmConfig.put(Integer.valueOf(1074), 
      new AlarmConfig(1074, 
      Msg.getString("Alarm.TYPE_MUX_REMOVED_SFP1"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_REMOVED_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1075), 
      new AlarmConfig(1075, 
      Msg.getString("Alarm.TYPE_MUX_CHANGED_SFP1"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_CHANGED_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1076), 
      new AlarmConfig(1076, 
      Msg.getString("Alarm.TYPE_MUX_REMOVED_SFP2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_REMOVED_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1077), 
      new AlarmConfig(1077, 
      Msg.getString("Alarm.TYPE_MUX_CHANGED_SFP2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_CHANGED_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1078), 
      new AlarmConfig(1078, 
      Msg.getString("Alarm.TYPE_MUX_REMOVED_SFP3"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_REMOVED_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1079), 
      new AlarmConfig(1079, 
      Msg.getString("Alarm.TYPE_MUX_CHANGED_SFP3"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_CHANGED_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1080), 
      new AlarmConfig(1080, 
      Msg.getString("Alarm.TYPE_MUX_REMOVED_SFP4"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_REMOVED_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1081), 
      new AlarmConfig(1081, 
      Msg.getString("Alarm.TYPE_MUX_CHANGED_SFP4"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUX_CHANGED_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1480), 
      new AlarmConfig(1480, Msg.getString("Alarm.TYPE_TRP_B1_SDH1"), false, 
      40, Msg.getString("AlarmDescr.TYPE_TRP_B1_SDH1"), 5000L));

    alarmConfig.put(Integer.valueOf(1082), 
      new AlarmConfig(1082, Msg.getString("Alarm.TYPE_MUX_B1_SDH1"), false, 
      40, Msg.getString("AlarmDescr.TYPE_MUX_B1_SDH1"), 5000L));

    alarmConfig.put(Integer.valueOf(1083), 
      new AlarmConfig(1083, Msg.getString("Alarm.TYPE_MUX_B1_SDH2"), false, 
      40, Msg.getString("AlarmDescr.TYPE_MUX_B1_SDH2"), 5000L));

    alarmConfig.put(Integer.valueOf(1084), 
      new AlarmConfig(1084, Msg.getString("Alarm.TYPE_MUX_B1_SDH3"), false, 
      40, Msg.getString("AlarmDescr.TYPE_MUX_B1_SDH3"), 5000L));

    alarmConfig.put(Integer.valueOf(1085), 
      new AlarmConfig(1085, Msg.getString("Alarm.TYPE_MUX_B1_SDH4"), false, 
      40, Msg.getString("AlarmDescr.TYPE_MUX_B1_SDH4"), 5000L));

    alarmConfig.put(Integer.valueOf(1150), 
      new AlarmConfig(1150, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TEMPERATURE_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TEMPERATURE_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1151), 
      new AlarmConfig(1151, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TEMPERATURE_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TEMPERATURE_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1152), 
      new AlarmConfig(1152, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TENSION_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TENSION_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1153), 
      new AlarmConfig(1153, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TENSION_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TENSION_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1154), 
      new AlarmConfig(1154, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_CURRENT_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_CURRENT_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1155), 
      new AlarmConfig(1155, 
      Msg.getString("Alarm.TYPE_MUX_LOW_CURRENT_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_CURRENT_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1156), 
      new AlarmConfig(1156, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_POUT_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_POUT_SFP1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1157), 
      new AlarmConfig(1157, 
      Msg.getString("Alarm.TYPE_MUX_LOW_POUT_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_POUT_SFP1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1158), 
      new AlarmConfig(1158, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_PIN_SFP1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_PIN_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1159), 
      new AlarmConfig(1159, Msg.getString("Alarm.TYPE_MUX_LOW_PIN_SFP1"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_MUX_LOW_PIN_SFP1"), 5000L));

    alarmConfig.put(Integer.valueOf(1160), 
      new AlarmConfig(1160, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TEMPERATURE_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TEMPERATURE_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1161), 
      new AlarmConfig(1161, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TEMPERATURE_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TEMPERATURE_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1162), 
      new AlarmConfig(1162, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TENSION_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TENSION_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1163), 
      new AlarmConfig(1163, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TENSION_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TENSION_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1164), 
      new AlarmConfig(1164, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_CURRENT_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_CURRENT_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1165), 
      new AlarmConfig(1165, 
      Msg.getString("Alarm.TYPE_MUX_LOW_CURRENT_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_CURRENT_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1166), 
      new AlarmConfig(1166, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_POUT_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_POUT_SFP2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1167), 
      new AlarmConfig(1167, 
      Msg.getString("Alarm.TYPE_MUX_LOW_POUT_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_POUT_SFP2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1168), 
      new AlarmConfig(1168, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_PIN_SFP2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_PIN_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1169), 
      new AlarmConfig(1169, Msg.getString("Alarm.TYPE_MUX_LOW_PIN_SFP2"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_MUX_LOW_PIN_SFP2"), 5000L));

    alarmConfig.put(Integer.valueOf(1170), 
      new AlarmConfig(1170, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TEMPERATURE_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TEMPERATURE_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1171), 
      new AlarmConfig(1171, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TEMPERATURE_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TEMPERATURE_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1172), 
      new AlarmConfig(1172, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TENSION_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TENSION_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1173), 
      new AlarmConfig(1173, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TENSION_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TENSION_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1174), 
      new AlarmConfig(1174, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_CURRENT_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_CURRENT_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1175), 
      new AlarmConfig(1175, 
      Msg.getString("Alarm.TYPE_MUX_LOW_CURRENT_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_CURRENT_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1176), 
      new AlarmConfig(1176, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_POUT_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_POUT_SFP3"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1177), 
      new AlarmConfig(1177, 
      Msg.getString("Alarm.TYPE_MUX_LOW_POUT_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_POUT_SFP3"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1178), 
      new AlarmConfig(1178, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_PIN_SFP3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_PIN_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1179), 
      new AlarmConfig(1179, Msg.getString("Alarm.TYPE_MUX_LOW_PIN_SFP3"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_MUX_LOW_PIN_SFP3"), 5000L));

    alarmConfig.put(Integer.valueOf(1180), 
      new AlarmConfig(1180, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TEMPERATURE_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TEMPERATURE_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1181), 
      new AlarmConfig(1181, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TEMPERATURE_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TEMPERATURE_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1182), 
      new AlarmConfig(1182, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_TENSION_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_TENSION_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1183), 
      new AlarmConfig(1183, 
      Msg.getString("Alarm.TYPE_MUX_LOW_TENSION_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_TENSION_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1184), 
      new AlarmConfig(1184, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_CURRENT_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_CURRENT_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1185), 
      new AlarmConfig(1185, 
      Msg.getString("Alarm.TYPE_MUX_LOW_CURRENT_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_CURRENT_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1186), 
      new AlarmConfig(1186, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_POUT_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_POUT_SFP4"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1187), 
      new AlarmConfig(1187, 
      Msg.getString("Alarm.TYPE_MUX_LOW_POUT_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOW_POUT_SFP4"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1188), 
      new AlarmConfig(1188, 
      Msg.getString("Alarm.TYPE_MUX_HIGH_PIN_SFP4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUX_HIGH_PIN_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1189), 
      new AlarmConfig(1189, Msg.getString("Alarm.TYPE_MUX_LOW_PIN_SFP4"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_MUX_LOW_PIN_SFP4"), 5000L));

    alarmConfig.put(Integer.valueOf(1200), 
      new AlarmConfig(1200, Msg.getString("Alarm.TYPE_CURRENT_ALARM1"), false, 
      40, Msg.getString("AlarmDescr.TYPE_CURRENT_ALARM1"), 5000L));
    alarmConfig.put(Integer.valueOf(1201), 
      new AlarmConfig(1201, Msg.getString("Alarm.TYPE_CURRENT_ALARM2"), false, 
      40, Msg.getString("AlarmDescr.TYPE_CURRENT_ALARM2"), 5000L));
    alarmConfig.put(Integer.valueOf(1202), 
      new AlarmConfig(1202, Msg.getString("Alarm.TYPE_CURRENT_ALARM3"), false, 
      40, Msg.getString("AlarmDescr.TYPE_CURRENT_ALARM3"), 5000L));
    alarmConfig.put(Integer.valueOf(1203), 
      new AlarmConfig(1203, Msg.getString("Alarm.TYPE_CURRENT_ALARM4"), false, 
      40, Msg.getString("AlarmDescr.TYPE_CURRENT_ALARM4"), 5000L));
    alarmConfig.put(Integer.valueOf(1212), 
      new AlarmConfig(1212, 
      Msg.getString("Alarm.TYPE_CURRENT_AGC_ALARM1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_CURRENT_AGC_ALARM1"), 5000L));
    alarmConfig.put(Integer.valueOf(1213), 
      new AlarmConfig(1213, 
      Msg.getString("Alarm.TYPE_CURRENT_AGC_ALARM2"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_CURRENT_AGC_ALARM2"), 5000L));
    alarmConfig.put(Integer.valueOf(1214), 
      new AlarmConfig(1214, 
      Msg.getString("Alarm.TYPE_CURRENT_AGC_ALARM3"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_CURRENT_AGC_ALARM3"), 5000L));
    alarmConfig.put(Integer.valueOf(1215), 
      new AlarmConfig(1215, 
      Msg.getString("Alarm.TYPE_CURRENT_AGC_ALARM4"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_CURRENT_AGC_ALARM4"), 5000L));
    alarmConfig.put(Integer.valueOf(1204), 
      new AlarmConfig(1204, 
      Msg.getString("Alarm.TYPE_TEMPERATURE_ALARM1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TEMPERATURE_ALARM1"), 5000L));
    alarmConfig.put(Integer.valueOf(1205), 
      new AlarmConfig(1205, 
      Msg.getString("Alarm.TYPE_TEMPERATURE_ALARM2"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TEMPERATURE_ALARM2"), 5000L));
    alarmConfig.put(Integer.valueOf(1206), 
      new AlarmConfig(1206, 
      Msg.getString("Alarm.TYPE_TEMPERATURE_ALARM3"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TEMPERATURE_ALARM3"), 5000L));
    alarmConfig.put(Integer.valueOf(1207), 
      new AlarmConfig(1207, 
      Msg.getString("Alarm.TYPE_TEMPERATURE_ALARM4"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_TEMPERATURE_ALARM4"), 5000L));
    alarmConfig.put(Integer.valueOf(1208), 
      new AlarmConfig(1208, Msg.getString("Alarm.TYPE_AMP_FAIL1"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_AMP_FAIL1"), 5000L));
    alarmConfig.put(Integer.valueOf(1209), 
      new AlarmConfig(1209, Msg.getString("Alarm.TYPE_AMP_FAIL2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_AMP_FAIL2"), 5000L));
    alarmConfig.put(Integer.valueOf(1210), 
      new AlarmConfig(1210, Msg.getString("Alarm.TYPE_AMP_FAIL3"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_AMP_FAIL3"), 5000L));
    alarmConfig.put(Integer.valueOf(1211), 
      new AlarmConfig(1211, Msg.getString("Alarm.TYPE_AMP_FAIL4"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_AMP_FAIL4"), 5000L));

    alarmConfig.put(Integer.valueOf(1300), 
      new AlarmConfig(1300, Msg.getString("Alarm.TYPE_NE_MOVE"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_NE_MOVE"), 5000L));

    alarmConfig.put(Integer.valueOf(1301), 
      new AlarmConfig(1301, 
      Msg.getString("Alarm.TYPE_CONN_ADMIN_CLOSE"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_CONN_ADMIN_CLOSE"), 5000L));

    alarmConfig.put(Integer.valueOf(1350), 
      new AlarmConfig(1350, Msg.getString("Alarm.TYPE_COMB_LOS_SDH"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH"), 5000L));
    alarmConfig.put(Integer.valueOf(1566), 
      new AlarmConfig(1566, Msg.getString("Alarm.TYPE_COMB_LOS_SDH1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH1"), 5000L));
    alarmConfig.put(Integer.valueOf(1567), 
      new AlarmConfig(1567, Msg.getString("Alarm.TYPE_COMB_LOS_SDH2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH2"), 5000L));
    alarmConfig.put(Integer.valueOf(1568), 
      new AlarmConfig(1568, Msg.getString("Alarm.TYPE_COMB_LOS_SDH3"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH3"), 5000L));
    alarmConfig.put(Integer.valueOf(1569), 
      new AlarmConfig(1569, Msg.getString("Alarm.TYPE_COMB_LOS_SDH4"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH4"), 5000L));
    alarmConfig.put(Integer.valueOf(1570), 
      new AlarmConfig(1570, Msg.getString("Alarm.TYPE_COMB_LOS_SDH5"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH5"), 5000L));
    alarmConfig.put(Integer.valueOf(1571), 
      new AlarmConfig(1571, Msg.getString("Alarm.TYPE_COMB_LOS_SDH6"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH6"), 5000L));
    alarmConfig.put(Integer.valueOf(1572), 
      new AlarmConfig(1572, Msg.getString("Alarm.TYPE_COMB_LOS_SDH7"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH7"), 5000L));
    alarmConfig.put(Integer.valueOf(1573), 
      new AlarmConfig(1573, Msg.getString("Alarm.TYPE_COMB_LOS_SDH8"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_LOS_SDH8"), 5000L));

    alarmConfig.put(Integer.valueOf(1543), 
      new AlarmConfig(1543, Msg.getString("Alarm.TYPE_COMB_LOF"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOF"), 5000L));

    alarmConfig.put(Integer.valueOf(1351), 
      new AlarmConfig(1351, Msg.getString("Alarm.TYPE_COMB_LOF_SDH"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH"), 5000L));
    alarmConfig.put(Integer.valueOf(1574), 
      new AlarmConfig(1574, Msg.getString("Alarm.TYPE_COMB_LOF_SDH1"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH1"), 5000L));
    alarmConfig.put(Integer.valueOf(1575), 
      new AlarmConfig(1575, Msg.getString("Alarm.TYPE_COMB_LOF_SDH2"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH2"), 5000L));
    alarmConfig.put(Integer.valueOf(1576), 
      new AlarmConfig(1576, Msg.getString("Alarm.TYPE_COMB_LOF_SDH3"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH3"), 5000L));
    alarmConfig.put(Integer.valueOf(1577), 
      new AlarmConfig(1577, Msg.getString("Alarm.TYPE_COMB_LOF_SDH4"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH4"), 5000L));
    alarmConfig.put(Integer.valueOf(1578), 
      new AlarmConfig(1578, Msg.getString("Alarm.TYPE_COMB_LOF_SDH5"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH5"), 5000L));
    alarmConfig.put(Integer.valueOf(1579), 
      new AlarmConfig(1579, Msg.getString("Alarm.TYPE_COMB_LOF_SDH6"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH6"), 5000L));
    alarmConfig.put(Integer.valueOf(1580), 
      new AlarmConfig(1580, Msg.getString("Alarm.TYPE_COMB_LOF_SDH7"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH7"), 5000L));
    alarmConfig.put(Integer.valueOf(1581), 
      new AlarmConfig(1581, Msg.getString("Alarm.TYPE_COMB_LOF_SDH8"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_LOF_SDH8"), 5000L));

    alarmConfig.put(Integer.valueOf(1352), 
      new AlarmConfig(1352, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH"), 5000L));
    alarmConfig.put(Integer.valueOf(1550), 
      new AlarmConfig(1550, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH1"), 5000L));
    alarmConfig.put(Integer.valueOf(1551), 
      new AlarmConfig(1551, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH2"), 5000L));
    alarmConfig.put(Integer.valueOf(1552), 
      new AlarmConfig(1552, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH3"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH3"), 5000L));
    alarmConfig.put(Integer.valueOf(1553), 
      new AlarmConfig(1553, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH4"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH4"), 5000L));
    alarmConfig.put(Integer.valueOf(1554), 
      new AlarmConfig(1554, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH5"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH5"), 5000L));
    alarmConfig.put(Integer.valueOf(1555), 
      new AlarmConfig(1555, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH6"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH6"), 5000L));
    alarmConfig.put(Integer.valueOf(1556), 
      new AlarmConfig(1556, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH7"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH7"), 5000L));
    alarmConfig.put(Integer.valueOf(1557), 
      new AlarmConfig(1557, Msg.getString("Alarm.TYPE_COMB_FAIL_SDH8"), false, 
      60, Msg.getString("AlarmDescr.TYPE_COMB_FAIL_SDH8"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1353), 
      new AlarmConfig(1353, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH"), 5000L));
    alarmConfig.put(Integer.valueOf(1558), 
      new AlarmConfig(1558, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH1"), 5000L));
    alarmConfig.put(Integer.valueOf(1559), 
      new AlarmConfig(1559, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH2"), 5000L));
    alarmConfig.put(Integer.valueOf(1560), 
      new AlarmConfig(1560, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH3"), 5000L));
    alarmConfig.put(Integer.valueOf(1561), 
      new AlarmConfig(1561, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH4"), 5000L));
    alarmConfig.put(Integer.valueOf(1562), 
      new AlarmConfig(1562, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH5"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH5"), 5000L));
    alarmConfig.put(Integer.valueOf(1563), 
      new AlarmConfig(1563, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH6"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH6"), 5000L));
    alarmConfig.put(Integer.valueOf(1564), 
      new AlarmConfig(1564, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH7"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH7"), 5000L));
    alarmConfig.put(Integer.valueOf(1565), 
      new AlarmConfig(1565, 
      Msg.getString("Alarm.TYPE_COMB_LASEROFF_SDH8"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF_SDH8"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1354), 
      new AlarmConfig(1354, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH"), 5000L));
    alarmConfig.put(Integer.valueOf(1582), 
      new AlarmConfig(1582, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH1"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH1"), 5000L));
    alarmConfig.put(Integer.valueOf(1583), 
      new AlarmConfig(1583, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH2"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH2"), 5000L));
    alarmConfig.put(Integer.valueOf(1584), 
      new AlarmConfig(1584, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH3"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH3"), 5000L));
    alarmConfig.put(Integer.valueOf(1585), 
      new AlarmConfig(1585, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH4"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH4"), 5000L));
    alarmConfig.put(Integer.valueOf(1586), 
      new AlarmConfig(1586, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH5"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH5"), 5000L));
    alarmConfig.put(Integer.valueOf(1587), 
      new AlarmConfig(1587, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH6"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH6"), 5000L));
    alarmConfig.put(Integer.valueOf(1588), 
      new AlarmConfig(1588, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH7"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH7"), 5000L));
    alarmConfig.put(Integer.valueOf(1589), 
      new AlarmConfig(1589, 
      Msg.getString("Alarm.TYPE_COMB_LOS_SYNC_SDH8"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS_SYNC_SDH8"), 5000L));

    alarmConfig.put(Integer.valueOf(1355), 
      new AlarmConfig(1355, Msg.getString("Alarm.TYPE_COMB_J0_SDH"), false, 
      30, Msg.getString("AlarmDescr.TYPE_COMB_J0_SDH"), 5000L));

    alarmConfig.put(Integer.valueOf(1356), 
      new AlarmConfig(1356, 
      Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP"), 5000L));
    alarmConfig.put(Integer.valueOf(1598), 
      new AlarmConfig(1598, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP1"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP1"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1599), 
      new AlarmConfig(1599, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP2"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP2"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1600), 
      new AlarmConfig(1600, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP3"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP3"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1601), 
      new AlarmConfig(1601, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP4"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP4"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1602), 
      new AlarmConfig(1602, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP5"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP5"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1603), 
      new AlarmConfig(1603, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP6"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP6"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1604), 
      new AlarmConfig(1604, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP7"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP7"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1605), 
      new AlarmConfig(1605, Msg.getString("Alarm.TYPE_COMB_REMOVED_SFP8"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REMOVED_SFP8"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1358), 
      new AlarmConfig(1358, Msg.getString("Alarm.TYPE_COMB_B1_SDH"), false, 
      40, Msg.getString("AlarmDescr.TYPE_COMB_B1_SDH"), 5000L));

    alarmConfig.put(Integer.valueOf(1359), 
      new AlarmConfig(1359, 
      Msg.getString("Alarm.TYPE_COMB_HIGH_TEMPERATURE_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_HIGH_TEMPERATURE_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1360), 
      new AlarmConfig(1360, 
      Msg.getString("Alarm.TYPE_COMB_LOW_TEMPERATURE_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOW_TEMPERATURE_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1361), 
      new AlarmConfig(1361, 
      Msg.getString("Alarm.TYPE_COMB_HIGH_TENSION_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_HIGH_TENSION_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1362), 
      new AlarmConfig(1362, 
      Msg.getString("Alarm.TYPE_COMB_LOW_TENSION_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOW_TENSION_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1363), 
      new AlarmConfig(1363, 
      Msg.getString("Alarm.TYPE_COMB_HIGH_CURRENT_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_HIGH_CURRENT_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1364), 
      new AlarmConfig(1364, 
      Msg.getString("Alarm.TYPE_COMB_LOW_CURRENT_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOW_CURRENT_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1365), 
      new AlarmConfig(1365, 
      Msg.getString("Alarm.TYPE_COMB_HIGH_POUT_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_HIGH_POUT_SFP"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1366), 
      new AlarmConfig(1366, 
      Msg.getString("Alarm.TYPE_COMB_LOW_POUT_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOW_POUT_SFP"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1367), 
      new AlarmConfig(1367, 
      Msg.getString("Alarm.TYPE_COMB_HIGH_PIN_SFP"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_HIGH_PIN_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1368), 
      new AlarmConfig(1368, Msg.getString("Alarm.TYPE_COMB_LOW_PIN_SFP"), 
      false, 50, Msg.getString("AlarmDescr.TYPE_COMB_LOW_PIN_SFP"), 5000L));

    alarmConfig.put(Integer.valueOf(1369), 
      new AlarmConfig(1369, 
      Msg.getString("Alarm.TYPE_COMB_CONFIG_CHANGED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_COMB_CONFIG_CHANGED"), 5000L));

    alarmConfig.put(Integer.valueOf(1370), 
      new AlarmConfig(1370, 
      Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP"), 5000L));
    alarmConfig.put(Integer.valueOf(1590), 
      new AlarmConfig(1590, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP1"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP1"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1591), 
      new AlarmConfig(1591, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP2"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP2"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1592), 
      new AlarmConfig(1592, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP3"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP3"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1593), 
      new AlarmConfig(1593, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP4"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP4"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1594), 
      new AlarmConfig(1594, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP5"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP5"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1595), 
      new AlarmConfig(1595, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP6"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP6"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1596), 
      new AlarmConfig(1596, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP7"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP7"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1597), 
      new AlarmConfig(1597, Msg.getString("Alarm.TYPE_COMB_CHANGED_SFP8"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_CHANGED_SFP8"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1407), 
      new AlarmConfig(1407, Msg.getString("Alarm.TYPE_COMB_LOS"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_COMB_LOS"), 5000L));

    alarmConfig.put(Integer.valueOf(1408), 
      new AlarmConfig(1408, Msg.getString("Alarm.TYPE_COMB_LASEROFF"), false, 
      50, Msg.getString("AlarmDescr.TYPE_COMB_LASEROFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1371), 
      new AlarmConfig(1371, 
      Msg.getString("Alarm.TYPE_COMB_OVER_TEMPERATURE"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_COMB_OVER_TEMPERATURE"), 5000L));

    alarmConfig.put(Integer.valueOf(1409), 
      new AlarmConfig(1409, Msg.getString("Alarm.TYPE_NE_BACKUP"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_NE_BACKUP"), 5000L));

    alarmConfig.put(Integer.valueOf(1410), 
      new AlarmConfig(1410, Msg.getString("Alarm.TYPE_NE_RESTORE"), false, 
      30, Msg.getString("AlarmDescr.TYPE_NE_RESTORE"), 5000L));

    alarmConfig.put(Integer.valueOf(1411), 
      new AlarmConfig(1411, Msg.getString("Alarm.TYPE_MUX_LOS"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_MUX_LOS"), 5000L));

    alarmConfig.put(Integer.valueOf(1412), 
      new AlarmConfig(1412, Msg.getString("Alarm.TYPE_HISTORY_ALARM"), false, 
      30, Msg.getString("AlarmDescr.TYPE_HISTORY_ALARM"), 5000L));

    alarmConfig.put(Integer.valueOf(1413), 
      new AlarmConfig(1413, Msg.getString("Alarm.TYPE_HISTORY_ALARM_END"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_HISTORY_ALARM_END"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(1414), 
      new AlarmConfig(1414, Msg.getString("Alarm.TYPE_HISTORY_COMMAND"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_HISTORY_COMMAND"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1415), 
      new AlarmConfig(1415, 
      Msg.getString("Alarm.TYPE_HISTORY_COMMAND_END"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_HISTORY_COMMAND_END"), 5000L));

    alarmConfig.put(Integer.valueOf(1416), 
      new AlarmConfig(1416, 
      Msg.getString("Alarm.TYPE_HISTORY_PERFORMANCE"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_HISTORY_PERFORMANCE"), 5000L));

    alarmConfig.put(Integer.valueOf(1417), 
      new AlarmConfig(1417, 
      Msg.getString("Alarm.TYPE_HISTORY_PERFORMANCE_END"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_HISTORY_PERFORMANCE_END"), 5000L));

    alarmConfig.put(Integer.valueOf(1418), 
      new AlarmConfig(1418, 
      Msg.getString("Alarm.TYPE_HISTORY_LAST_ALARM"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_HISTORY_LAST_ALARM"), 5000L));

    alarmConfig.put(Integer.valueOf(1419), 
      new AlarmConfig(1419, 
      Msg.getString("Alarm.TYPE_HISTORY_LAST_ALARM_END"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_HISTORY_LAST_ALARM_END"), 5000L));

    alarmConfig.put(Integer.valueOf(1460), 
      new AlarmConfig(1460, 
      Msg.getString("Alarm.TYPE_ROAM_COMMOM_OUTPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_COMMOM_OUTPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1461), 
      new AlarmConfig(1461, 
      Msg.getString("Alarm.TYPE_ROAM_DROP_OUTPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_DROP_OUTPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1462), 
      new AlarmConfig(1462, 
      Msg.getString("Alarm.TYPE_DEMUX_COMMOM_INPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_COMMOM_INPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1478), 
      new AlarmConfig(1478, 
      Msg.getString("Alarm.TYPE_ROAM_ROADM_5329_RESTART"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ROADM_5329_RESTART"), 5000L));
    alarmConfig.put(Integer.valueOf(1479), 
      new AlarmConfig(1479, 
      Msg.getString("Alarm.TYPE_ROAM_ROADM_UC_RESTART"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ROADM_UC_RESTART"), 5000L));

    alarmConfig
      .put(
      Integer.valueOf(1464), 
      new AlarmConfig(
      1464, 
      Msg.getString("Alarm.TYPE_ROAM_MUX_AWG_TEMPERATURE_FAIL_HIGH"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_MUX_AWG_TEMPERATURE_FAIL_HIGH"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(1465), 
      new AlarmConfig(
      1465, 
      Msg.getString("Alarm.TYPE_ROAM_DEMUX_AWG_TEMPERATURE_FAIL_HIGH"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_DEMUX_AWG_TEMPERATURE_FAIL_HIGH"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1466), 
      new AlarmConfig(1466, 
      Msg.getString("Alarm.TYPE_DEMUX_AWG_TEMPERATURE_FAIL_HIGH"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_AWG_TEMPERATURE_FAIL_HIGH"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(1450), 
      new AlarmConfig(
      1450, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW"), 
      false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1451), 
      new AlarmConfig(1451, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(1452), 
      new AlarmConfig(
      1452, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1453), 
      new AlarmConfig(1453, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1467), 
      new AlarmConfig(1467, 
      Msg.getString("Alarm.TYPE_DEMUX_VOA_ATTENUATION_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_VOA_ATTENUATION_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1454), 
      new AlarmConfig(1454, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1455), 
      new AlarmConfig(1455, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1456), 
      new AlarmConfig(1456, Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL"), 
      false, 60, Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1457), 
      new AlarmConfig(1457, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_INPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_INPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(1458), 
      new AlarmConfig(
      1458, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_OUTPUT_POWER_FAIL_LOW"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_OUTPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1459), 
      new AlarmConfig(1459, 
      Msg.getString("Alarm.TYPE_ROAM_COMMOM_INPUT_POWER_FAIL_LOW"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_COMMOM_INPUT_POWER_FAIL_LOW"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1468), 
      new AlarmConfig(1468, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1469), 
      new AlarmConfig(1469, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE"), 5000L));

    alarmConfig.put(Integer.valueOf(1472), 
      new AlarmConfig(1472, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1473), 
      new AlarmConfig(1473, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1474), 
      new AlarmConfig(1474, Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP"), 
      false, 40, Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP"), 5000L));

    alarmConfig.put(Integer.valueOf(1475), 
      new AlarmConfig(1475, 
      Msg.getString("Alarm.TYPE_ROADM_ROAM_NOT_CALIBRATED"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_ROAM_NOT_CALIBRATED"), 5000L));
    alarmConfig.put(Integer.valueOf(1476), 
      new AlarmConfig(1476, 
      Msg.getString("Alarm.TYPE_ROADM_ROAM_IN_CALIBRATION"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_ROAM_IN_CALIBRATION"), 5000L));
    alarmConfig.put(Integer.valueOf(1477), 
      new AlarmConfig(1477, 
      Msg.getString("Alarm.TYPE_ROADM_ROAM_CALIBRATED"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_ROAM_CALIBRATED"), 5000L));

    alarmConfig.put(Integer.valueOf(1471), 
      new AlarmConfig(1471, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL"), 5000L));

    alarmConfig.put(Integer.valueOf(1422), 
      new AlarmConfig(1422, 
      Msg.getString("Alarm.TYPE_TRP_NEW_CHANNEL1"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_NEW_CHANNEL1"), 5000L));
    alarmConfig.put(Integer.valueOf(1423), 
      new AlarmConfig(1423, 
      Msg.getString("Alarm.TYPE_TRP_NEW_CHANNEL2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_NEW_CHANNEL2"), 5000L));

    alarmConfig.put(Integer.valueOf(1034), 
      new AlarmConfig(1034, 
      Msg.getString("Alarm.TYPE_FANG8_OVERHEAT1"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_FANG8_OVERHEAT1"), 5000L));

    alarmConfig.put(Integer.valueOf(1035), 
      new AlarmConfig(1035, 
      Msg.getString("Alarm.TYPE_FANG8_OVERHEAT2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_FANG8_OVERHEAT2"), 5000L));

    alarmConfig.put(Integer.valueOf(1036), 
      new AlarmConfig(1036, Msg.getString("Alarm.TYPE_FANG8_FAIL1"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL1"), 5000L));

    alarmConfig.put(Integer.valueOf(1037), 
      new AlarmConfig(1037, Msg.getString("Alarm.TYPE_FANG8_FAIL2"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL2"), 5000L));

    alarmConfig.put(Integer.valueOf(1038), 
      new AlarmConfig(1038, Msg.getString("Alarm.TYPE_FANG8_FAIL3"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL3"), 5000L));

    alarmConfig.put(Integer.valueOf(1039), 
      new AlarmConfig(1039, Msg.getString("Alarm.TYPE_FANG8_FAIL4"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL4"), 5000L));

    alarmConfig.put(Integer.valueOf(1040), 
      new AlarmConfig(1040, Msg.getString("Alarm.TYPE_FANG8_FAIL5"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL5"), 5000L));

    alarmConfig.put(Integer.valueOf(1041), 
      new AlarmConfig(1041, Msg.getString("Alarm.TYPE_FANG8_FAIL6"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL6"), 5000L));

    alarmConfig.put(Integer.valueOf(1042), 
      new AlarmConfig(1042, Msg.getString("Alarm.TYPE_FANG8_FAIL7"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL7"), 5000L));

    alarmConfig.put(Integer.valueOf(1043), 
      new AlarmConfig(1043, Msg.getString("Alarm.TYPE_FANG8_FAIL8"), false, 
      50, Msg.getString("AlarmDescr.TYPE_FANG8_FAIL8"), 5000L));

    alarmConfig.put(Integer.valueOf(1544), 
      new AlarmConfig(1544, Msg.getString("Alarm.TYPE_FANG8_OFF"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_FANG8_OFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1046), 
      new AlarmConfig(1046, 
      Msg.getString("Alarm.TYPE_MUXDEMUXGRNoVoa_AWG_FAIL"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUXDEMUXGRNoVoa_AWG_FAIL"), 5000L));

    alarmConfig.put(Integer.valueOf(1044), 
      new AlarmConfig(1044, 
      Msg.getString("Alarm.TYPE_MUXDEMUXGRNoVoa_OVERHEAT"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_MUXDEMUXGRNoVoa_OVERHEAT"), 5000L));

    alarmConfig.put(Integer.valueOf(1045), 
      new AlarmConfig(1045, 
      Msg.getString("Alarm.TYPE_MUXDEMUXGRNoVoa_NOT_READY"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUXDEMUXGRNoVoa_NOT_READY"), 5000L));

    alarmConfig.put(Integer.valueOf(1049), 
      new AlarmConfig(1049, 
      Msg.getString("Alarm.TYPE_MUXDEMUXVOA_AWG_FAIL"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_MUXDEMUXVOA_AWG_FAIL"), 5000L));

    alarmConfig.put(Integer.valueOf(1047), 
      new AlarmConfig(1047, 
      Msg.getString("Alarm.TYPE_MUXDEMUXVOA_OVERHEAT"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_MUXDEMUXVOA_OVERHEAT"), 5000L));

    alarmConfig.put(Integer.valueOf(1048), 
      new AlarmConfig(1048, 
      Msg.getString("Alarm.TYPE_MUXDEMUXVOA_NOT_READY"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_MUXDEMUXVOA_NOT_READY"), 5000L));

    alarmConfig.put(Integer.valueOf(1470), 
      new AlarmConfig(1470, Msg.getString("Alarm.TYPE_NE_MOVED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_NE_MOVED"), 5000L));

    alarmConfig.put(Integer.valueOf(1511), 
      new AlarmConfig(1511, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_TENSAO_A"), false, 
      50, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_TENSAO_A"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1512), 
      new AlarmConfig(1512, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_TENSAO_B"), false, 
      50, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_TENSAO_B"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1505), 
      new AlarmConfig(1505, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_TENSAO_A"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_TENSAO_A"), 5000L));

    alarmConfig.put(Integer.valueOf(1506), 
      new AlarmConfig(1506, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_TENSAO_B"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_TENSAO_B"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1507), 
      new AlarmConfig(1507, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_TENSAO_TOTAL"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_TENSAO_TOTAL"), 5000L));

    alarmConfig.put(Integer.valueOf(1508), 
      new AlarmConfig(1508, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_LOW_TENSAO_A"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_LOW_TENSAO_A"), 5000L));

    alarmConfig.put(Integer.valueOf(1509), 
      new AlarmConfig(1509, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_LOW_TENSAO_B"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_LOW_TENSAO_B"), 5000L));

    alarmConfig.put(Integer.valueOf(1510), 
      new AlarmConfig(1510, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_LOW_TENSAO_TOTAL"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_LOW_TENSAO_TOTAL"), 5000L));

    alarmConfig.put(Integer.valueOf(1493), 
      new AlarmConfig(1493, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_1"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1494), 
      new AlarmConfig(1494, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_2"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_2"), 5000L));

    alarmConfig.put(Integer.valueOf(1495), 
      new AlarmConfig(1495, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_3"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_3"), 5000L));

    alarmConfig.put(Integer.valueOf(1496), 
      new AlarmConfig(1496, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_4"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_4"), 5000L));

    alarmConfig.put(Integer.valueOf(1497), 
      new AlarmConfig(1497, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_5"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_5"), 5000L));

    alarmConfig.put(Integer.valueOf(1498), 
      new AlarmConfig(1498, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_6"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_6"), 5000L));

    alarmConfig.put(Integer.valueOf(1499), 
      new AlarmConfig(1499, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_7"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_7"), 5000L));

    alarmConfig.put(Integer.valueOf(1500), 
      new AlarmConfig(1500, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_8"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_8"), 5000L));

    alarmConfig.put(Integer.valueOf(1501), 
      new AlarmConfig(1501, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_9"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_9"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1502), 
      new AlarmConfig(1502, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_10"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_10"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1503), 
      new AlarmConfig(1503, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_11"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_11"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1504), 
      new AlarmConfig(1504, 
      Msg.getString("Alarm.TYPE_PPM_CIRCUITO_ABERTO_VIA_12"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_CIRCUITO_ABERTO_VIA_12"), 5000L));

    alarmConfig.put(Integer.valueOf(1481), 
      new AlarmConfig(1481, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_1"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_1"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1482), 
      new AlarmConfig(1482, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_2"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_2"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1483), 
      new AlarmConfig(1483, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_3"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_3"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1484), 
      new AlarmConfig(1484, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_4"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_4"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1485), 
      new AlarmConfig(1485, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_5"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_5"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1486), 
      new AlarmConfig(1486, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_6"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_6"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1487), 
      new AlarmConfig(1487, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_7"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_7"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1488), 
      new AlarmConfig(1488, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_8"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_8"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1489), 
      new AlarmConfig(1489, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_9"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_9"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1490), 
      new AlarmConfig(1490, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_10"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_10"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1491), 
      new AlarmConfig(1491, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_11"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_11"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1492), 
      new AlarmConfig(1492, 
      Msg.getString("Alarm.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_12"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_PPM_FAIL_HIGH_CORRENTE_VIA_12"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1513), 
      new AlarmConfig(1513, Msg.getString("Alarm.TYPE_SCMGRC_LOS_1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRC_LOS_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1514), 
      new AlarmConfig(1514, Msg.getString("Alarm.TYPE_SCMGRC_LOF_1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRC_LOF_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1517), 
      new AlarmConfig(1517, Msg.getString("Alarm.TYPE_SCMGRC_LOS_2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRC_LOS_2"), 5000L));

    alarmConfig.put(Integer.valueOf(1518), 
      new AlarmConfig(1518, Msg.getString("Alarm.TYPE_SCMGRC_LOF_2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRC_LOF_2"), 5000L));

    alarmConfig.put(Integer.valueOf(1516), 
      new AlarmConfig(1516, Msg.getString("Alarm.TYPE_SCMGRC_BDI_1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRC_BDI_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1520), 
      new AlarmConfig(1520, Msg.getString("Alarm.TYPE_SCMGRC_BDI_2"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRC_BDI_2"), 5000L));

    alarmConfig.put(Integer.valueOf(1521), 
      new AlarmConfig(1521, Msg.getString("Alarm.TYPE_SCMGRT_LOS_1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRT_LOS_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1522), 
      new AlarmConfig(1522, Msg.getString("Alarm.TYPE_SCMGRT_LOF_1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRT_LOF_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1524), 
      new AlarmConfig(1524, Msg.getString("Alarm.TYPE_SCMGRT_BDI_1"), false, 
      60, Msg.getString("AlarmDescr.TYPE_SCMGRT_BDI_1"), 5000L));

    alarmConfig.put(Integer.valueOf(1527), 
      new AlarmConfig(1527, Msg.getString("Alarm.TYPE_BLOCK_ON"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_BLOCK_ON"), 5000L));

    alarmConfig.put(Integer.valueOf(1528), 
      new AlarmConfig(1528, Msg.getString("Alarm.TYPE_BLOCK_OFF"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_BLOCK_OFF"), 5000L));

    alarmConfig.put(Integer.valueOf(1529), 
      new AlarmConfig(1529, Msg.getString("Alarm.TYPE_BLOCK_USER"), false, 
      30, Msg.getString("AlarmDescr.TYPE_BLOCK_USER"), 5000L));

    alarmConfig.put(Integer.valueOf(1530), 
      new AlarmConfig(1530, Msg.getString("Alarm.TYPE_TRP_LINK_DOWN"), false, 
      50, Msg.getString("AlarmDescr.TYPE_TRP_LINK_DOWN"), 5000L));
    alarmConfig
      .put(Integer.valueOf(1533), 
      new AlarmConfig(1533, 
      Msg.getString("Alarm.TYPE_TRP_MAC_RECEIVE_LOCAL_FAULT"), false, 
      50, 
      Msg.getString("AlarmDescr.TYPE_TRP_MAC_RECEIVE_LOCAL_FAULT"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1534), 
      new AlarmConfig(1534, 
      Msg.getString("Alarm.TYPE_TRP_MAC_RECEIVE_REMOTE_FAULT"), false, 
      50, 
      Msg.getString("AlarmDescr.TYPE_TRP_MAC_RECEIVE_REMOTE_FAULT"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1531), 
      new AlarmConfig(1531, 
      Msg.getString("Alarm.TYPE_TRP_RATE_CHANGED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_RATE_CHANGED"), 5000L));
    alarmConfig
      .put(Integer.valueOf(1532), 
      new AlarmConfig(1532, 
      Msg.getString("Alarm.TYPE_TRP_MAX_FRAME_SIZE_CHANGED"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_TRP_MAX_FRAME_SIZE_CHANGED"), 5000L));
    alarmConfig.put(Integer.valueOf(1535), 
      new AlarmConfig(1535, Msg.getString("Alarm.TYPE_TRP_J0_RX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_J0_RX"), 5000L));
    alarmConfig.put(Integer.valueOf(1536), 
      new AlarmConfig(1536, Msg.getString("Alarm.TYPE_TRP_J0_TX"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_J0_TX"), 5000L));
    alarmConfig.put(Integer.valueOf(1537), 
      new AlarmConfig(1537, Msg.getString("Alarm.TYPE_TRP_RS_TIM"), false, 
      60, Msg.getString("AlarmDescr.TYPE_TRP_RS_TIM"), 5000L));
    alarmConfig.put(Integer.valueOf(1540), 
      new AlarmConfig(1540, Msg.getString("Alarm.TYPE_TRP_ENC_RS_TIM"), false, 
      30, Msg.getString("AlarmDescr.TYPE_TRP_ENC_RS_TIM"), 5000L));
    alarmConfig.put(Integer.valueOf(1541), 
      new AlarmConfig(1541, 
      Msg.getString("Alarm.TYPE_TRP_J0_MODE_FINISHED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_TRP_J0_MODE_FINISHED"), 5000L));
    alarmConfig.put(Integer.valueOf(1623), 
      new AlarmConfig(1623, Msg.getString("Alarm.TYPE_TRP_REBOOT"), false, 
      30, Msg.getString("AlarmDescr.TYPE_TRP_REBOOT"), 5000L));

    alarmConfig.put(Integer.valueOf(1542), 
      new AlarmConfig(1542, 
      Msg.getString("Alarm.TYPE_DISCOVERY_DISABLED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_DISCOVERY_DISABLED"), 5000L));

    alarmConfig.put(Integer.valueOf(1631), 
      new AlarmConfig(1631, Msg.getString("Alarm.TYPE_ODP_CABLE_FAIL"), false, 
      60, Msg.getString("AlarmDescr.TYPE_ODP_CABLE_FAIL"), 5000L));
    alarmConfig.put(Integer.valueOf(1632), 
      new AlarmConfig(1632, Msg.getString("Alarm.TYPE_ODP_COMM_LOS"), false, 
      60, Msg.getString("AlarmDescr.TYPE_ODP_COMM_LOS"), 5000L));
    alarmConfig.put(Integer.valueOf(1638), 
      new AlarmConfig(1638, 
      Msg.getString("Alarm.TYPE_ODP_NEIGHBOUR_CABLE_FAIL"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_ODP_NEIGHBOUR_CABLE_FAIL"), 5000L));
    alarmConfig.put(Integer.valueOf(1639), 
      new AlarmConfig(1639, 
      Msg.getString("Alarm.TYPE_ODP_NEIGHBOUR_COMM_LOS"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_ODP_NEIGHBOUR_COMM_LOS"), 5000L));
    alarmConfig.put(Integer.valueOf(1630), 
      new AlarmConfig(1630, Msg.getString("Alarm.TYPE_ODP_CONFIG_ERROR"), 
      false, 60, Msg.getString("AlarmDescr.TYPE_ODP_CONFIG_ERROR"), 
      5000L));
    alarmConfig.put(Integer.valueOf(1634), 
      new AlarmConfig(1634, 
      Msg.getString("Alarm.TYPE_ODP_PATH_STATE_CHANGED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ODP_PATH_STATE_CHANGED"), 5000L));
    alarmConfig.put(Integer.valueOf(1635), 
      new AlarmConfig(1635, 
      Msg.getString("Alarm.TYPE_ODP_PATH_TYPE_CHANGED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ODP_PATH_TYPE_CHANGED"), 5000L));
    alarmConfig.put(Integer.valueOf(1633), 
      new AlarmConfig(1633, 
      Msg.getString("Alarm.TYPE_ODP_NEIGHBOUR_ID_CHANGED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ODP_NEIGHBOUR_ID_CHANGED"), 5000L));
    alarmConfig.put(Integer.valueOf(1636), 
      new AlarmConfig(1636, 
      Msg.getString("Alarm.TYPE_ODP_WAITTORESTORE_ENABLED"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ODP_WAITTORESTORE_ENABLED"), 5000L));
    alarmConfig.put(Integer.valueOf(1637), 
      new AlarmConfig(1637, Msg.getString("Alarm.TYPE_ODP_DISABLED"), false, 
      30, Msg.getString("AlarmDescr.TYPE_ODP_DISABLED"), 5000L));
    alarmConfig
      .put(Integer.valueOf(1640), 
      new AlarmConfig(1640, 
      Msg.getString("Alarm.TYPE_ODP_LASER_OFF_ODP"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ODP_LASER_OFF_ODP"), 5000L));
    alarmConfig.put(Integer.valueOf(1641), 
      new AlarmConfig(1641, 
      Msg.getString("Alarm.TYPE_ODP_LASER_OFF_ODP_COMB"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ODP_LASER_OFF_ODP_COMB"), 5000L));
    alarmConfig.put(Integer.valueOf(1642), 
      new AlarmConfig(1642, 
      Msg.getString("Alarm.TYPE_ODP_MANUAL_LASER_ON"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ODP_MANUAL_LASER_ON"), 5000L));

    alarmConfig
      .put(Integer.valueOf(1643), 
      new AlarmConfig(1643, Msg.getString("Alarm.TYPE_COMB_REBOOT_GFP"), 
      false, 30, Msg.getString("AlarmDescr.TYPE_COMB_REBOOT_GFP"), 
      5000L));

    alarmConfig.put(Integer.valueOf(1644), 
      new AlarmConfig(1644, 
      Msg.getString("Alarm.TYPE_TRP_OVER_TEMPERATURE"), false, 50, 
      Msg.getString("AlarmDescr.TYPE_TRP_OVER_TEMPERATURE"), 5000L));

    alarmConfig.put(Integer.valueOf(1658), 
      new AlarmConfig(1658, 
      Msg.getString("Alarm.TYPE_SCMGRC_ALS_SOUTH_ACTING"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_SCMGRC_ALS_SOUTH_ACTING"), 5000L));
    alarmConfig.put(Integer.valueOf(1659), 
      new AlarmConfig(1659, 
      Msg.getString("Alarm.TYPE_SCMGRC_ALS_NORTH_ACTING"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_SCMGRC_ALS_NORTH_ACTING"), 5000L));
    alarmConfig
      .put(Integer.valueOf(1657), 
      new AlarmConfig(1657, 
      Msg.getString("Alarm.TYPE_SCMGRT_ALS_ACTING"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_SCMGRT_ALS_ACTING"), 5000L));

    alarmConfig
      .put(
      Integer.valueOf(2010), 
      new AlarmConfig(
      2010, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_1"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_1"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2011), 
      new AlarmConfig(
      2011, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_2"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_2"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2012), 
      new AlarmConfig(
      2012, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_3"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_3"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2013), 
      new AlarmConfig(
      2013, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_4"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_4"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2014), 
      new AlarmConfig(
      2014, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_5"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_5"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2015), 
      new AlarmConfig(
      2015, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_6"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_6"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2016), 
      new AlarmConfig(
      2016, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_7"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_7"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2017), 
      new AlarmConfig(
      2017, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_8"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_8"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2018), 
      new AlarmConfig(
      2018, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_9"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_9"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2019), 
      new AlarmConfig(
      2019, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_10"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_10"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2020), 
      new AlarmConfig(
      2020, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_11"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_11"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2021), 
      new AlarmConfig(
      2021, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_12"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_12"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2022), 
      new AlarmConfig(
      2022, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_13"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_13"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2023), 
      new AlarmConfig(
      2023, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_14"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_14"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2024), 
      new AlarmConfig(
      2024, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_15"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_15"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2025), 
      new AlarmConfig(
      2025, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_16"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_16"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2026), 
      new AlarmConfig(
      2026, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_17"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_17"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2027), 
      new AlarmConfig(
      2027, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_18"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_18"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2028), 
      new AlarmConfig(
      2028, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_19"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_19"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2029), 
      new AlarmConfig(
      2029, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_20"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_20"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2030), 
      new AlarmConfig(
      2030, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_21"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_21"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2031), 
      new AlarmConfig(
      2031, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_22"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_22"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2032), 
      new AlarmConfig(
      2032, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_23"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_23"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2033), 
      new AlarmConfig(
      2033, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_24"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_24"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2034), 
      new AlarmConfig(
      2034, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_25"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_25"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2035), 
      new AlarmConfig(
      2035, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_26"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_26"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2036), 
      new AlarmConfig(
      2036, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_27"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_27"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2037), 
      new AlarmConfig(
      2037, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_28"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_28"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2038), 
      new AlarmConfig(
      2038, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_29"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_29"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2039), 
      new AlarmConfig(
      2039, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_30"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_30"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2040), 
      new AlarmConfig(
      2040, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_31"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_31"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2041), 
      new AlarmConfig(
      2041, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_32"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_32"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2042), 
      new AlarmConfig(
      2042, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_33"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_33"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2043), 
      new AlarmConfig(
      2043, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_34"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_34"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2044), 
      new AlarmConfig(
      2044, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_35"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_35"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2045), 
      new AlarmConfig(
      2045, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_36"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_36"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2046), 
      new AlarmConfig(
      2046, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_37"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_37"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2047), 
      new AlarmConfig(
      2047, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_38"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_38"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2048), 
      new AlarmConfig(
      2048, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_39"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_39"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2049), 
      new AlarmConfig(
      2049, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_40"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_OUTPUT_POWER_FAIL_LOW_40"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2050), 
      new AlarmConfig(
      2050, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_1"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_1"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2051), 
      new AlarmConfig(
      2051, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_2"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_2"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2052), 
      new AlarmConfig(
      2052, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_3"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_3"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2053), 
      new AlarmConfig(
      2053, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_4"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_4"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2054), 
      new AlarmConfig(
      2054, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_5"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_5"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2055), 
      new AlarmConfig(
      2055, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_6"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_6"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2056), 
      new AlarmConfig(
      2056, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_7"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_7"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2057), 
      new AlarmConfig(
      2057, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_8"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_8"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2058), 
      new AlarmConfig(
      2058, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_9"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_9"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2059), 
      new AlarmConfig(
      2059, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_10"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_10"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2060), 
      new AlarmConfig(
      2060, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_11"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_11"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2061), 
      new AlarmConfig(
      2061, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_12"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_12"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2062), 
      new AlarmConfig(
      2062, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_13"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_13"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2063), 
      new AlarmConfig(
      2063, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_14"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_14"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2064), 
      new AlarmConfig(
      2064, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_15"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_15"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2065), 
      new AlarmConfig(
      2065, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_16"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_16"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2066), 
      new AlarmConfig(
      2066, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_17"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_17"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2067), 
      new AlarmConfig(
      2067, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_18"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_18"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2068), 
      new AlarmConfig(
      2068, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_19"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_19"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2069), 
      new AlarmConfig(
      2069, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_20"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_20"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2070), 
      new AlarmConfig(
      2070, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_21"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_21"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2071), 
      new AlarmConfig(
      2071, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_22"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_22"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2072), 
      new AlarmConfig(
      2072, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_23"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_23"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2073), 
      new AlarmConfig(
      2073, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_24"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_24"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2074), 
      new AlarmConfig(
      2074, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_25"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_25"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2075), 
      new AlarmConfig(
      2075, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_26"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_26"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2076), 
      new AlarmConfig(
      2076, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_27"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_27"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2077), 
      new AlarmConfig(
      2077, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_28"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_28"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2078), 
      new AlarmConfig(
      2078, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_29"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_29"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2079), 
      new AlarmConfig(
      2079, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_30"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_30"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2080), 
      new AlarmConfig(
      2080, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_31"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_31"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2081), 
      new AlarmConfig(
      2081, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_32"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_32"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2082), 
      new AlarmConfig(
      2082, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_33"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_33"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2083), 
      new AlarmConfig(
      2083, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_34"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_34"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2084), 
      new AlarmConfig(
      2084, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_35"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_35"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2085), 
      new AlarmConfig(
      2085, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_36"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_36"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2086), 
      new AlarmConfig(
      2086, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_37"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_37"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2087), 
      new AlarmConfig(
      2087, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_38"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_38"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2088), 
      new AlarmConfig(
      2088, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_39"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_39"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2089), 
      new AlarmConfig(
      2089, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_40"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_OUTPUT_POWER_FAIL_LOW_40"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2090), 
      new AlarmConfig(
      2090, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_1"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_1"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2091), 
      new AlarmConfig(
      2091, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_2"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_2"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2092), 
      new AlarmConfig(
      2092, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_3"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_3"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2093), 
      new AlarmConfig(
      2093, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_4"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_4"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2094), 
      new AlarmConfig(
      2094, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_5"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_5"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2095), 
      new AlarmConfig(
      2095, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_6"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_6"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2096), 
      new AlarmConfig(
      2096, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_7"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_7"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2097), 
      new AlarmConfig(
      2097, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_8"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_8"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2098), 
      new AlarmConfig(
      2098, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_9"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_9"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2099), 
      new AlarmConfig(
      2099, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_10"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_10"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2100), 
      new AlarmConfig(
      2100, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_11"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_11"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2101), 
      new AlarmConfig(
      2101, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_12"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_12"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2102), 
      new AlarmConfig(
      2102, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_13"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_13"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2103), 
      new AlarmConfig(
      2103, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_14"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_14"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2104), 
      new AlarmConfig(
      2104, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_15"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_15"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2105), 
      new AlarmConfig(
      2105, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_16"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_16"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2106), 
      new AlarmConfig(
      2106, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_17"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_17"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2107), 
      new AlarmConfig(
      2107, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_18"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_18"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2108), 
      new AlarmConfig(
      2108, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_19"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_19"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2109), 
      new AlarmConfig(
      2109, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_20"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_20"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2110), 
      new AlarmConfig(
      2110, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_21"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_21"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2111), 
      new AlarmConfig(
      2111, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_22"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_22"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2112), 
      new AlarmConfig(
      2112, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_23"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_23"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2113), 
      new AlarmConfig(
      2113, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_24"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_24"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2114), 
      new AlarmConfig(
      2114, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_25"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_25"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2115), 
      new AlarmConfig(
      2115, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_26"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_26"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2116), 
      new AlarmConfig(
      2116, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_27"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_27"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2117), 
      new AlarmConfig(
      2117, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_28"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_28"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2118), 
      new AlarmConfig(
      2118, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_29"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_29"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2119), 
      new AlarmConfig(
      2119, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_30"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_30"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2120), 
      new AlarmConfig(
      2120, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_31"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_31"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2121), 
      new AlarmConfig(
      2121, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_32"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_32"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2122), 
      new AlarmConfig(
      2122, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_33"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_33"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2123), 
      new AlarmConfig(
      2123, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_34"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_34"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2124), 
      new AlarmConfig(
      2124, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_35"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_35"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2125), 
      new AlarmConfig(
      2125, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_36"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_36"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2126), 
      new AlarmConfig(
      2126, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_37"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_37"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2127), 
      new AlarmConfig(
      2127, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_38"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_38"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2128), 
      new AlarmConfig(
      2128, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_39"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_39"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2129), 
      new AlarmConfig(
      2129, 
      Msg.getString("Alarm.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_40"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_EXPRESS_CH_ATTENUATION_FAIL_LOW_40"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2130), 
      new AlarmConfig(
      2130, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_1"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_1"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2131), 
      new AlarmConfig(
      2131, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_2"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_2"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2132), 
      new AlarmConfig(
      2132, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_3"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_3"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2133), 
      new AlarmConfig(
      2133, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_4"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_4"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2134), 
      new AlarmConfig(
      2134, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_5"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_5"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2135), 
      new AlarmConfig(
      2135, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_6"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_6"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2136), 
      new AlarmConfig(
      2136, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_7"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_7"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2137), 
      new AlarmConfig(
      2137, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_8"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_8"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2138), 
      new AlarmConfig(
      2138, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_9"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_9"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2139), 
      new AlarmConfig(
      2139, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_10"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_10"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2140), 
      new AlarmConfig(
      2140, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_11"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_11"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2141), 
      new AlarmConfig(
      2141, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_12"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_12"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2142), 
      new AlarmConfig(
      2142, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_13"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_13"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2143), 
      new AlarmConfig(
      2143, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_14"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_14"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2144), 
      new AlarmConfig(
      2144, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_15"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_15"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2145), 
      new AlarmConfig(
      2145, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_16"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_16"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2146), 
      new AlarmConfig(
      2146, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_17"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_17"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2147), 
      new AlarmConfig(
      2147, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_18"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_18"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2148), 
      new AlarmConfig(
      2148, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_19"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_19"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2149), 
      new AlarmConfig(
      2149, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_20"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_20"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2150), 
      new AlarmConfig(
      2150, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_21"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_21"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2151), 
      new AlarmConfig(
      2151, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_22"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_22"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2152), 
      new AlarmConfig(
      2152, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_23"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_23"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2153), 
      new AlarmConfig(
      2153, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_24"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_24"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2154), 
      new AlarmConfig(
      2154, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_25"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_25"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2155), 
      new AlarmConfig(
      2155, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_26"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_26"), 
      5000L));

    alarmConfig.put(2156, new AlarmConfig( 2156, Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_27"), 
      false,  60, Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_27"), 5000L));

    alarmConfig
      .put(
      Integer.valueOf(2157), 
      new AlarmConfig(
      2157, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_28"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_28"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2158), 
      new AlarmConfig(
      2158, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_29"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_29"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2159), 
      new AlarmConfig(
      2159, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_30"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_30"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2160), 
      new AlarmConfig(
      2160, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_31"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_31"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2161), 
      new AlarmConfig(
      2161, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_32"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_32"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2162), 
      new AlarmConfig(
      2162, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_33"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_33"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2163), 
      new AlarmConfig(
      2163, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_34"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_34"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2164), 
      new AlarmConfig(
      2164, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_35"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_35"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2165), 
      new AlarmConfig(
      2165, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_36"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_36"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2166), 
      new AlarmConfig(
      2166, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_37"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_37"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2167), 
      new AlarmConfig(
      2167, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_38"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_38"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2168), 
      new AlarmConfig(
      2168, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_39"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_39"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2169), 
      new AlarmConfig(
      2169, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_40"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_ATTENUATION_FAIL_LOW_40"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2170), 
      new AlarmConfig(
      2170, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_1"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_1"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2171), 
      new AlarmConfig(
      2171, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_2"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_2"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2172), 
      new AlarmConfig(
      2172, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_3"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_3"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2173), 
      new AlarmConfig(
      2173, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_4"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_4"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2174), 
      new AlarmConfig(
      2174, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_5"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_5"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2175), 
      new AlarmConfig(
      2175, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_6"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_6"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2176), 
      new AlarmConfig(
      2176, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_7"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_7"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2177), 
      new AlarmConfig(
      2177, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_8"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_8"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2178), 
      new AlarmConfig(
      2178, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_9"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_9"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2179), 
      new AlarmConfig(
      2179, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_10"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_10"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2180), 
      new AlarmConfig(
      2180, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_11"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_11"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2181), 
      new AlarmConfig(
      2181, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_12"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_12"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2182), 
      new AlarmConfig(
      2182, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_13"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_13"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2183), 
      new AlarmConfig(
      2183, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_14"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_14"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2184), 
      new AlarmConfig(
      2184, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_15"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_15"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2185), 
      new AlarmConfig(
      2185, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_16"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_16"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2186), 
      new AlarmConfig(
      2186, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_17"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_17"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2187), 
      new AlarmConfig(
      2187, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_18"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_18"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2188), 
      new AlarmConfig(
      2188, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_19"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_19"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2189), 
      new AlarmConfig(
      2189, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_20"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_20"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2190), 
      new AlarmConfig(
      2190, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_21"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_21"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2191), 
      new AlarmConfig(
      2191, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_22"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_22"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2192), 
      new AlarmConfig(
      2192, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_23"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_23"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2193), 
      new AlarmConfig(
      2193, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_24"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_24"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2194), 
      new AlarmConfig(
      2194, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_25"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_25"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2195), 
      new AlarmConfig(
      2195, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_26"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_26"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2196), 
      new AlarmConfig(
      2196, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_27"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_27"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2197), 
      new AlarmConfig(
      2197, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_28"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_28"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2198), 
      new AlarmConfig(
      2198, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_29"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_29"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2199), 
      new AlarmConfig(
      2199, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_30"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_30"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2200), 
      new AlarmConfig(
      2200, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_31"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_31"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2201), 
      new AlarmConfig(
      2201, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_32"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_32"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2202), 
      new AlarmConfig(
      2202, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_33"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_33"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2203), 
      new AlarmConfig(
      2203, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_34"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_34"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2204), 
      new AlarmConfig(
      2204, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_35"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_35"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2205), 
      new AlarmConfig(
      2205, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_36"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_36"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2206), 
      new AlarmConfig(
      2206, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_37"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_37"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2207), 
      new AlarmConfig(
      2207, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_38"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_38"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2208), 
      new AlarmConfig(
      2208, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_39"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_39"), 
      5000L));

    alarmConfig
      .put(
      Integer.valueOf(2209), 
      new AlarmConfig(
      2209, 
      Msg.getString("Alarm.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_40"), 
      false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_ROAM_ADD_CH_INPUT_POWER_FAIL_LOW_40"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2210), 
      new AlarmConfig(2210, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_1"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_1"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2211), 
      new AlarmConfig(2211, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_2"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_2"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2212), 
      new AlarmConfig(2212, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_3"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_3"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2213), 
      new AlarmConfig(2213, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_4"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_4"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2214), 
      new AlarmConfig(2214, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_5"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_5"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2215), 
      new AlarmConfig(2215, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_6"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_6"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2216), 
      new AlarmConfig(2216, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_7"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_7"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2217), 
      new AlarmConfig(2217, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_8"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_8"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2218), 
      new AlarmConfig(2218, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_9"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_9"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2219), 
      new AlarmConfig(2219, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_10"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_10"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2220), 
      new AlarmConfig(2220, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_11"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_11"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2221), 
      new AlarmConfig(2221, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_12"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_12"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2222), 
      new AlarmConfig(2222, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_13"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_13"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2223), 
      new AlarmConfig(2223, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_14"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_14"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2224), 
      new AlarmConfig(2224, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_15"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_15"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2225), 
      new AlarmConfig(2225, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_16"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_16"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2226), 
      new AlarmConfig(2226, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_17"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_17"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2227), 
      new AlarmConfig(2227, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_18"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_18"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2228), 
      new AlarmConfig(2228, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_19"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_19"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2229), 
      new AlarmConfig(2229, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_20"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_20"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2230), 
      new AlarmConfig(2230, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_21"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_21"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2231), 
      new AlarmConfig(2231, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_22"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_22"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2232), 
      new AlarmConfig(2232, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_23"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_23"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2233), 
      new AlarmConfig(2233, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_24"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_24"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2234), 
      new AlarmConfig(2234, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_25"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_25"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2235), 
      new AlarmConfig(2235, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_26"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_26"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2236), 
      new AlarmConfig(2236, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_27"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_27"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2237), 
      new AlarmConfig(2237, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_28"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_28"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2238), 
      new AlarmConfig(2238, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_29"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_29"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2239), 
      new AlarmConfig(2239, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_30"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_30"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2240), 
      new AlarmConfig(2240, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_31"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_31"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2241), 
      new AlarmConfig(2241, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_32"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_32"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2242), 
      new AlarmConfig(2242, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_33"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_33"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2243), 
      new AlarmConfig(2243, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_34"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_34"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2244), 
      new AlarmConfig(2244, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_35"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_35"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2245), 
      new AlarmConfig(2245, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_36"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_36"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2246), 
      new AlarmConfig(2246, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_37"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_37"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2247), 
      new AlarmConfig(2247, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_38"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_38"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2248), 
      new AlarmConfig(2248, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_39"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_39"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2249), 
      new AlarmConfig(2249, 
      Msg.getString("Alarm.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_40"), false, 
      60, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CH_OUTPUT_POWER_FAIL_LOW_40"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2250), 
      new AlarmConfig(2250, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_1"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_1"), 5000L));

    alarmConfig.put(Integer.valueOf(2251), 
      new AlarmConfig(2251, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_2"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_2"), 5000L));

    alarmConfig.put(Integer.valueOf(2252), 
      new AlarmConfig(2252, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_3"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_3"), 5000L));

    alarmConfig.put(Integer.valueOf(2253), 
      new AlarmConfig(2253, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_4"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_4"), 5000L));

    alarmConfig.put(Integer.valueOf(2254), 
      new AlarmConfig(2254, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_5"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_5"), 5000L));

    alarmConfig.put(Integer.valueOf(2255), 
      new AlarmConfig(2255, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_6"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_6"), 5000L));

    alarmConfig.put(Integer.valueOf(2256), 
      new AlarmConfig(2256, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_7"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_7"), 5000L));

    alarmConfig.put(Integer.valueOf(2257), 
      new AlarmConfig(2257, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_8"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_8"), 5000L));

    alarmConfig.put(Integer.valueOf(2258), 
      new AlarmConfig(2258, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_9"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_9"), 5000L));

    alarmConfig.put(Integer.valueOf(2259), 
      new AlarmConfig(2259, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_10"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_10"), 5000L));

    alarmConfig.put(Integer.valueOf(2260), 
      new AlarmConfig(2260, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_11"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_11"), 5000L));

    alarmConfig.put(Integer.valueOf(2261), 
      new AlarmConfig(2261, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_12"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_12"), 5000L));

    alarmConfig.put(Integer.valueOf(2262), 
      new AlarmConfig(2262, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_13"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_13"), 5000L));

    alarmConfig.put(Integer.valueOf(2263), 
      new AlarmConfig(2263, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_14"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_14"), 5000L));

    alarmConfig.put(Integer.valueOf(2264), 
      new AlarmConfig(2264, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_15"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_15"), 5000L));

    alarmConfig.put(Integer.valueOf(2265), 
      new AlarmConfig(2265, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_16"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_16"), 5000L));

    alarmConfig.put(Integer.valueOf(2266), 
      new AlarmConfig(2266, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_17"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_17"), 5000L));

    alarmConfig.put(Integer.valueOf(2267), 
      new AlarmConfig(2267, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_18"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_18"), 5000L));

    alarmConfig.put(Integer.valueOf(2268), 
      new AlarmConfig(2268, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_19"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_19"), 5000L));

    alarmConfig.put(Integer.valueOf(2269), 
      new AlarmConfig(2269, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_20"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_20"), 5000L));

    alarmConfig.put(Integer.valueOf(2270), 
      new AlarmConfig(2270, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_21"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_21"), 5000L));

    alarmConfig.put(Integer.valueOf(2271), 
      new AlarmConfig(2271, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_22"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_22"), 5000L));

    alarmConfig.put(Integer.valueOf(2272), 
      new AlarmConfig(2272, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_23"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_23"), 5000L));

    alarmConfig.put(Integer.valueOf(2273), 
      new AlarmConfig(2273, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_24"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_24"), 5000L));

    alarmConfig.put(Integer.valueOf(2274), 
      new AlarmConfig(2274, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_25"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_25"), 5000L));

    alarmConfig.put(Integer.valueOf(2275), 
      new AlarmConfig(2275, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_26"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_26"), 5000L));

    alarmConfig.put(Integer.valueOf(2276), 
      new AlarmConfig(2276, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_27"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_27"), 5000L));

    alarmConfig.put(Integer.valueOf(2277), 
      new AlarmConfig(2277, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_28"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_28"), 5000L));

    alarmConfig.put(Integer.valueOf(2278), 
      new AlarmConfig(2278, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_29"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_29"), 5000L));

    alarmConfig.put(Integer.valueOf(2279), 
      new AlarmConfig(2279, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_30"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_30"), 5000L));

    alarmConfig.put(Integer.valueOf(2280), 
      new AlarmConfig(2280, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_31"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_31"), 5000L));

    alarmConfig.put(Integer.valueOf(2281), 
      new AlarmConfig(2281, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_32"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_32"), 5000L));

    alarmConfig.put(Integer.valueOf(2282), 
      new AlarmConfig(2282, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_33"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_33"), 5000L));

    alarmConfig.put(Integer.valueOf(2283), 
      new AlarmConfig(2283, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_34"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_34"), 5000L));

    alarmConfig.put(Integer.valueOf(2284), 
      new AlarmConfig(2284, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_35"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_35"), 5000L));

    alarmConfig.put(Integer.valueOf(2285), 
      new AlarmConfig(2285, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_36"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_36"), 5000L));

    alarmConfig.put(Integer.valueOf(2286), 
      new AlarmConfig(2286, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_37"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_37"), 5000L));

    alarmConfig.put(Integer.valueOf(2287), 
      new AlarmConfig(2287, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_38"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_38"), 5000L));

    alarmConfig.put(Integer.valueOf(2288), 
      new AlarmConfig(2288, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_39"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_39"), 5000L));

    alarmConfig.put(Integer.valueOf(2289), 
      new AlarmConfig(2289, 
      Msg.getString("Alarm.TYPE_DEGRADED_CHANNEL_40"), false, 60, 
      Msg.getString("AlarmDescr.TYPE_DEGRADED_CHANNEL_40"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2290), 
      new AlarmConfig(2290, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_1"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2291), 
      new AlarmConfig(2291, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_2"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2292), 
      new AlarmConfig(2292, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_3"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_3"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2293), 
      new AlarmConfig(2293, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_4"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_4"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2294), 
      new AlarmConfig(2294, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_5"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_5"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2295), 
      new AlarmConfig(2295, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_6"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_6"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2296), 
      new AlarmConfig(2296, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_7"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_7"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2297), 
      new AlarmConfig(2297, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_8"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_8"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2298), 
      new AlarmConfig(2298, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_9"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_9"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2299), 
      new AlarmConfig(2299, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_10"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_10"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2300), 
      new AlarmConfig(2300, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_11"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_11"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2301), 
      new AlarmConfig(2301, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_12"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_12"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2302), 
      new AlarmConfig(2302, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_13"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_13"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2303), 
      new AlarmConfig(2303, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_14"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_14"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2304), 
      new AlarmConfig(2304, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_15"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_15"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2305), 
      new AlarmConfig(2305, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_16"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_16"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2306), 
      new AlarmConfig(2306, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_17"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_17"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2307), 
      new AlarmConfig(2307, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_18"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_18"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2308), 
      new AlarmConfig(2308, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_19"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_19"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2309), 
      new AlarmConfig(2309, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_20"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_20"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2310), 
      new AlarmConfig(2310, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_21"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_21"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2311), 
      new AlarmConfig(2311, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_22"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_22"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2312), 
      new AlarmConfig(2312, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_23"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_23"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2313), 
      new AlarmConfig(2313, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_24"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_24"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2314), 
      new AlarmConfig(2314, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_25"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_25"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2315), 
      new AlarmConfig(2315, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_26"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_26"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2316), 
      new AlarmConfig(2316, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_27"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_27"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2317), 
      new AlarmConfig(2317, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_28"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_28"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2318), 
      new AlarmConfig(2318, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_29"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_29"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2319), 
      new AlarmConfig(2319, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_30"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_30"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2320), 
      new AlarmConfig(2320, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_31"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_31"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2321), 
      new AlarmConfig(2321, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_32"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_32"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2322), 
      new AlarmConfig(2322, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_33"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_33"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2323), 
      new AlarmConfig(2323, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_34"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_34"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2324), 
      new AlarmConfig(2324, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_35"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_35"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2325), 
      new AlarmConfig(2325, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_36"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_36"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2326), 
      new AlarmConfig(2326, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_37"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_37"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2327), 
      new AlarmConfig(2327, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_38"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_38"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2328), 
      new AlarmConfig(2328, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_39"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_39"), 
      5000L));

    alarmConfig
      .put(Integer.valueOf(2329), 
      new AlarmConfig(2329, 
      Msg.getString("Alarm.TYPE_ROADM_AUTO_SWITCHING_OFF_40"), false, 
      30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_AUTO_SWITCHING_OFF_40"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2330), 
      new AlarmConfig(2330, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_1"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_1"), 5000L));

    alarmConfig.put(Integer.valueOf(2331), 
      new AlarmConfig(2331, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_2"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_2"), 5000L));

    alarmConfig.put(Integer.valueOf(2332), 
      new AlarmConfig(2332, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_3"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_3"), 5000L));

    alarmConfig.put(Integer.valueOf(2333), 
      new AlarmConfig(2333, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_4"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_4"), 5000L));

    alarmConfig.put(Integer.valueOf(2334), 
      new AlarmConfig(2334, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_5"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_5"), 5000L));

    alarmConfig.put(Integer.valueOf(2335), 
      new AlarmConfig(2335, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_6"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_6"), 5000L));

    alarmConfig.put(Integer.valueOf(2336), 
      new AlarmConfig(2336, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_7"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_7"), 5000L));

    alarmConfig.put(Integer.valueOf(2337), 
      new AlarmConfig(2337, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_8"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_8"), 5000L));

    alarmConfig.put(Integer.valueOf(2338), 
      new AlarmConfig(2338, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_9"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_9"), 5000L));

    alarmConfig.put(Integer.valueOf(2339), 
      new AlarmConfig(2339, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_10"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_10"), 5000L));

    alarmConfig.put(Integer.valueOf(2340), 
      new AlarmConfig(2340, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_11"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_11"), 5000L));

    alarmConfig.put(Integer.valueOf(2341), 
      new AlarmConfig(2341, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_12"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_12"), 5000L));

    alarmConfig.put(Integer.valueOf(2342), 
      new AlarmConfig(2342, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_13"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_13"), 5000L));

    alarmConfig.put(Integer.valueOf(2343), 
      new AlarmConfig(2343, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_14"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_14"), 5000L));

    alarmConfig.put(Integer.valueOf(2344), 
      new AlarmConfig(2344, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_15"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_15"), 5000L));

    alarmConfig.put(Integer.valueOf(2345), 
      new AlarmConfig(2345, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_16"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_16"), 5000L));

    alarmConfig.put(Integer.valueOf(2346), 
      new AlarmConfig(2346, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_17"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_17"), 5000L));

    alarmConfig.put(Integer.valueOf(2347), 
      new AlarmConfig(2347, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_18"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_18"), 5000L));

    alarmConfig.put(Integer.valueOf(2348), 
      new AlarmConfig(2348, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_19"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_19"), 5000L));

    alarmConfig.put(Integer.valueOf(2349), 
      new AlarmConfig(2349, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_20"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_20"), 5000L));

    alarmConfig.put(Integer.valueOf(2350), 
      new AlarmConfig(2350, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_21"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_21"), 5000L));

    alarmConfig.put(Integer.valueOf(2351), 
      new AlarmConfig(2351, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_22"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_22"), 5000L));

    alarmConfig.put(Integer.valueOf(2352), 
      new AlarmConfig(2352, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_23"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_23"), 5000L));

    alarmConfig.put(Integer.valueOf(2353), 
      new AlarmConfig(2353, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_24"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_24"), 5000L));

    alarmConfig.put(Integer.valueOf(2354), 
      new AlarmConfig(2354, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_25"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_25"), 5000L));

    alarmConfig.put(Integer.valueOf(2355), 
      new AlarmConfig(2355, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_26"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_26"), 5000L));

    alarmConfig.put(Integer.valueOf(2356), 
      new AlarmConfig(2356, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_27"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_27"), 5000L));

    alarmConfig.put(Integer.valueOf(2357), 
      new AlarmConfig(2357, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_28"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_28"), 5000L));

    alarmConfig.put(Integer.valueOf(2358), 
      new AlarmConfig(2358, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_29"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_29"), 5000L));

    alarmConfig.put(Integer.valueOf(2359), 
      new AlarmConfig(2359, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_30"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_30"), 5000L));

    alarmConfig.put(Integer.valueOf(2360), 
      new AlarmConfig(2360, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_31"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_31"), 5000L));

    alarmConfig.put(Integer.valueOf(2361), 
      new AlarmConfig(2361, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_32"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_32"), 5000L));

    alarmConfig.put(Integer.valueOf(2362), 
      new AlarmConfig(2362, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_33"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_33"), 5000L));

    alarmConfig.put(Integer.valueOf(2363), 
      new AlarmConfig(2363, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_34"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_34"), 5000L));

    alarmConfig.put(Integer.valueOf(2364), 
      new AlarmConfig(2364, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_35"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_35"), 5000L));

    alarmConfig.put(Integer.valueOf(2365), 
      new AlarmConfig(2365, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_36"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_36"), 5000L));

    alarmConfig.put(Integer.valueOf(2366), 
      new AlarmConfig(2366, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_37"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_37"), 5000L));

    alarmConfig.put(Integer.valueOf(2367), 
      new AlarmConfig(2367, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_38"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_38"), 5000L));

    alarmConfig.put(Integer.valueOf(2368), 
      new AlarmConfig(2368, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_39"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_39"), 5000L));

    alarmConfig.put(Integer.valueOf(2369), 
      new AlarmConfig(2369, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHING_FAIL_40"), false, 30, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHING_FAIL_40"), 5000L));

    alarmConfig.put(Integer.valueOf(2370), 
      new AlarmConfig(2370, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_1"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_1"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2371), 
      new AlarmConfig(2371, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_2"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_2"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2372), 
      new AlarmConfig(2372, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_3"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_3"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2373), 
      new AlarmConfig(2373, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_4"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_4"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2374), 
      new AlarmConfig(2374, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_5"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_5"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2375), 
      new AlarmConfig(2375, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_6"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_6"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2376), 
      new AlarmConfig(2376, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_7"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_7"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2377), 
      new AlarmConfig(2377, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_8"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_8"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2378), 
      new AlarmConfig(2378, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_9"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_9"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2379), 
      new AlarmConfig(2379, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_10"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_10"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2380), 
      new AlarmConfig(2380, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_11"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_11"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2381), 
      new AlarmConfig(2381, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_12"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_12"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2382), 
      new AlarmConfig(2382, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_13"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_13"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2383), 
      new AlarmConfig(2383, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_14"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_14"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2384), 
      new AlarmConfig(2384, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_15"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_15"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2385), 
      new AlarmConfig(2385, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_16"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_16"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2386), 
      new AlarmConfig(2386, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_17"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_17"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2387), 
      new AlarmConfig(2387, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_18"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_18"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2388), 
      new AlarmConfig(2388, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_19"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_19"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2389), 
      new AlarmConfig(2389, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_20"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_20"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2390), 
      new AlarmConfig(2390, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_21"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_21"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2391), 
      new AlarmConfig(2391, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_22"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_22"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2392), 
      new AlarmConfig(2392, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_23"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_23"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2393), 
      new AlarmConfig(2393, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_24"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_24"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2394), 
      new AlarmConfig(2394, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_25"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_25"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2395), 
      new AlarmConfig(2395, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_26"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_26"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2396), 
      new AlarmConfig(2396, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_27"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_27"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2397), 
      new AlarmConfig(2397, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_28"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_28"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2398), 
      new AlarmConfig(2398, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_29"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_29"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2399), 
      new AlarmConfig(2399, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_30"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_30"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2400), 
      new AlarmConfig(2400, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_31"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_31"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2401), 
      new AlarmConfig(2401, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_32"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_32"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2402), 
      new AlarmConfig(2402, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_33"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_33"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2403), 
      new AlarmConfig(2403, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_34"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_34"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2404), 
      new AlarmConfig(2404, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_35"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_35"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2405), 
      new AlarmConfig(2405, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_36"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_36"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2406), 
      new AlarmConfig(2406, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_37"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_37"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2407), 
      new AlarmConfig(2407, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_38"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_38"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2408), 
      new AlarmConfig(2408, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_39"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_39"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2409), 
      new AlarmConfig(2409, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_INACTIVE_40"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_INACTIVE_40"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2410), 
      new AlarmConfig(2410, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_1"), 5000L));

    alarmConfig.put(Integer.valueOf(2411), 
      new AlarmConfig(2411, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_2"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_2"), 5000L));

    alarmConfig.put(Integer.valueOf(2412), 
      new AlarmConfig(2412, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_3"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_3"), 5000L));

    alarmConfig.put(Integer.valueOf(2413), 
      new AlarmConfig(2413, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_4"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_4"), 5000L));

    alarmConfig.put(Integer.valueOf(2414), 
      new AlarmConfig(2414, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_5"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_5"), 5000L));

    alarmConfig.put(Integer.valueOf(2415), 
      new AlarmConfig(2415, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_6"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_6"), 5000L));

    alarmConfig.put(Integer.valueOf(2416), 
      new AlarmConfig(2416, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_7"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_7"), 5000L));

    alarmConfig.put(Integer.valueOf(2417), 
      new AlarmConfig(2417, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_8"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_8"), 5000L));

    alarmConfig.put(Integer.valueOf(2418), 
      new AlarmConfig(2418, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_9"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_9"), 5000L));

    alarmConfig.put(Integer.valueOf(2419), 
      new AlarmConfig(2419, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_10"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_10"), 5000L));

    alarmConfig.put(Integer.valueOf(2420), 
      new AlarmConfig(2420, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_11"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_11"), 5000L));

    alarmConfig.put(Integer.valueOf(2421), 
      new AlarmConfig(2421, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_12"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_12"), 5000L));

    alarmConfig.put(Integer.valueOf(2422), 
      new AlarmConfig(2422, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_13"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_13"), 5000L));

    alarmConfig.put(Integer.valueOf(2423), 
      new AlarmConfig(2423, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_14"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_14"), 5000L));

    alarmConfig.put(Integer.valueOf(2424), 
      new AlarmConfig(2424, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_15"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_15"), 5000L));

    alarmConfig.put(Integer.valueOf(2425), 
      new AlarmConfig(2425, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_16"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_16"), 5000L));

    alarmConfig.put(Integer.valueOf(2426), 
      new AlarmConfig(2426, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_17"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_17"), 5000L));

    alarmConfig.put(Integer.valueOf(2427), 
      new AlarmConfig(2427, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_18"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_18"), 5000L));

    alarmConfig.put(Integer.valueOf(2428), 
      new AlarmConfig(2428, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_19"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_19"), 5000L));

    alarmConfig.put(Integer.valueOf(2429), 
      new AlarmConfig(2429, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_20"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_20"), 5000L));

    alarmConfig.put(Integer.valueOf(2430), 
      new AlarmConfig(2430, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_21"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_21"), 5000L));

    alarmConfig.put(Integer.valueOf(2431), 
      new AlarmConfig(2431, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_22"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_22"), 5000L));

    alarmConfig.put(Integer.valueOf(2432), 
      new AlarmConfig(2432, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_23"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_23"), 5000L));

    alarmConfig.put(Integer.valueOf(2433), 
      new AlarmConfig(2433, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_24"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_24"), 5000L));

    alarmConfig.put(Integer.valueOf(2434), 
      new AlarmConfig(2434, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_25"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_25"), 5000L));

    alarmConfig.put(Integer.valueOf(2435), 
      new AlarmConfig(2435, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_26"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_26"), 5000L));

    alarmConfig.put(Integer.valueOf(2436), 
      new AlarmConfig(2436, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_27"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_27"), 5000L));

    alarmConfig.put(Integer.valueOf(2437), 
      new AlarmConfig(2437, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_28"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_28"), 5000L));

    alarmConfig.put(Integer.valueOf(2438), 
      new AlarmConfig(2438, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_29"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_29"), 5000L));

    alarmConfig.put(Integer.valueOf(2439), 
      new AlarmConfig(2439, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_30"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_30"), 5000L));

    alarmConfig.put(Integer.valueOf(2440), 
      new AlarmConfig(2440, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_31"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_31"), 5000L));

    alarmConfig.put(Integer.valueOf(2441), 
      new AlarmConfig(2441, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_32"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_32"), 5000L));

    alarmConfig.put(Integer.valueOf(2442), 
      new AlarmConfig(2442, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_33"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_33"), 5000L));

    alarmConfig.put(Integer.valueOf(2443), 
      new AlarmConfig(2443, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_34"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_34"), 5000L));

    alarmConfig.put(Integer.valueOf(2444), 
      new AlarmConfig(2444, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_35"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_35"), 5000L));

    alarmConfig.put(Integer.valueOf(2445), 
      new AlarmConfig(2445, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_36"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_36"), 5000L));

    alarmConfig.put(Integer.valueOf(2446), 
      new AlarmConfig(2446, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_37"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_37"), 5000L));

    alarmConfig.put(Integer.valueOf(2447), 
      new AlarmConfig(2447, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_38"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_38"), 5000L));

    alarmConfig.put(Integer.valueOf(2448), 
      new AlarmConfig(2448, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_39"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_39"), 5000L));

    alarmConfig.put(Integer.valueOf(2449), 
      new AlarmConfig(2449, 
      Msg.getString("Alarm.TYPE_DEMUX_CALIBRATE_OFF_40"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_DEMUX_CALIBRATE_OFF_40"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2450), 
      new AlarmConfig(2450, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_1"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_1"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2451), 
      new AlarmConfig(2451, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_2"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_2"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2452), 
      new AlarmConfig(2452, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_3"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_3"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2453), 
      new AlarmConfig(2453, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_4"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_4"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2454), 
      new AlarmConfig(2454, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_5"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_5"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2455), 
      new AlarmConfig(2455, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_6"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_6"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2456), 
      new AlarmConfig(2456, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_7"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_7"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2457), 
      new AlarmConfig(2457, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_8"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_8"), 5000L));

    alarmConfig
      .put(Integer.valueOf(2458), 
      new AlarmConfig(2458, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_9"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_9"), 5000L));

    alarmConfig.put(Integer.valueOf(2459), 
      new AlarmConfig(2459, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_10"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_10"), 5000L));

    alarmConfig.put(Integer.valueOf(2460), 
      new AlarmConfig(2460, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_11"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_11"), 5000L));

    alarmConfig.put(Integer.valueOf(2461), 
      new AlarmConfig(2461, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_12"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_12"), 5000L));

    alarmConfig.put(Integer.valueOf(2462), 
      new AlarmConfig(2462, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_13"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_13"), 5000L));

    alarmConfig.put(Integer.valueOf(2463), 
      new AlarmConfig(2463, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_14"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_14"), 5000L));

    alarmConfig.put(Integer.valueOf(2464), 
      new AlarmConfig(2464, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_15"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_15"), 5000L));

    alarmConfig.put(Integer.valueOf(2465), 
      new AlarmConfig(2465, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_16"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_16"), 5000L));

    alarmConfig.put(Integer.valueOf(2466), 
      new AlarmConfig(2466, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_17"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_17"), 5000L));

    alarmConfig.put(Integer.valueOf(2467), 
      new AlarmConfig(2467, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_18"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_18"), 5000L));

    alarmConfig.put(Integer.valueOf(2468), 
      new AlarmConfig(2468, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_19"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_19"), 5000L));

    alarmConfig.put(Integer.valueOf(2469), 
      new AlarmConfig(2469, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_20"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_20"), 5000L));

    alarmConfig.put(Integer.valueOf(2470), 
      new AlarmConfig(2470, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_21"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_21"), 5000L));

    alarmConfig.put(Integer.valueOf(2471), 
      new AlarmConfig(2471, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_22"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_22"), 5000L));

    alarmConfig.put(Integer.valueOf(2472), 
      new AlarmConfig(2472, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_23"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_23"), 5000L));

    alarmConfig.put(Integer.valueOf(2473), 
      new AlarmConfig(2473, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_24"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_24"), 5000L));

    alarmConfig.put(Integer.valueOf(2474), 
      new AlarmConfig(2474, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_25"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_25"), 5000L));

    alarmConfig.put(Integer.valueOf(2475), 
      new AlarmConfig(2475, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_26"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_26"), 5000L));

    alarmConfig.put(Integer.valueOf(2476), 
      new AlarmConfig(2476, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_27"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_27"), 5000L));

    alarmConfig.put(Integer.valueOf(2477), 
      new AlarmConfig(2477, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_28"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_28"), 5000L));

    alarmConfig.put(Integer.valueOf(2478), 
      new AlarmConfig(2478, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_29"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_29"), 5000L));

    alarmConfig.put(Integer.valueOf(2479), 
      new AlarmConfig(2479, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_30"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_30"), 5000L));

    alarmConfig.put(Integer.valueOf(2480), 
      new AlarmConfig(2480, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_31"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_31"), 5000L));

    alarmConfig.put(Integer.valueOf(2481), 
      new AlarmConfig(2481, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_32"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_32"), 5000L));

    alarmConfig.put(Integer.valueOf(2482), 
      new AlarmConfig(2482, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_33"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_33"), 5000L));

    alarmConfig.put(Integer.valueOf(2483), 
      new AlarmConfig(2483, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_34"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_34"), 5000L));

    alarmConfig.put(Integer.valueOf(2484), 
      new AlarmConfig(2484, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_35"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_35"), 5000L));

    alarmConfig.put(Integer.valueOf(2485), 
      new AlarmConfig(2485, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_36"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_36"), 5000L));

    alarmConfig.put(Integer.valueOf(2486), 
      new AlarmConfig(2486, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_37"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_37"), 5000L));

    alarmConfig.put(Integer.valueOf(2487), 
      new AlarmConfig(2487, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_38"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_38"), 5000L));

    alarmConfig.put(Integer.valueOf(2488), 
      new AlarmConfig(2488, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_39"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_39"), 5000L));

    alarmConfig.put(Integer.valueOf(2489), 
      new AlarmConfig(2489, 
      Msg.getString("Alarm.TYPE_ROADM_OPEN_LOOP_40"), false, 40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_OPEN_LOOP_40"), 5000L));

    alarmConfig.put(Integer.valueOf(2490), 
      new AlarmConfig(2490, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_1"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_1"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2491), 
      new AlarmConfig(2491, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_2"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_2"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2492), 
      new AlarmConfig(2492, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_3"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_3"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2493), 
      new AlarmConfig(2493, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_4"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_4"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2494), 
      new AlarmConfig(2494, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_5"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_5"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2495), 
      new AlarmConfig(2495, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_6"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_6"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2496), 
      new AlarmConfig(2496, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_7"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_7"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2497), 
      new AlarmConfig(2497, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_8"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_8"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2498), 
      new AlarmConfig(2498, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_9"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_9"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2499), 
      new AlarmConfig(2499, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_10"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_10"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2500), 
      new AlarmConfig(2500, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_11"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_11"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2501), 
      new AlarmConfig(2501, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_12"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_12"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2502), 
      new AlarmConfig(2502, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_13"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_13"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2503), 
      new AlarmConfig(2503, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_14"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_14"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2504), 
      new AlarmConfig(2504, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_15"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_15"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2505), 
      new AlarmConfig(2505, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_16"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_16"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2506), 
      new AlarmConfig(2506, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_17"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_17"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2507), 
      new AlarmConfig(2507, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_18"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_18"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2508), 
      new AlarmConfig(2508, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_19"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_19"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2509), 
      new AlarmConfig(2509, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_20"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_20"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2510), 
      new AlarmConfig(2510, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_21"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_21"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2511), 
      new AlarmConfig(2511, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_22"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_22"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2512), 
      new AlarmConfig(2512, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_23"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_23"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2513), 
      new AlarmConfig(2513, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_24"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_24"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2514), 
      new AlarmConfig(2514, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_25"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_25"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2515), 
      new AlarmConfig(2515, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_26"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_26"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2516), 
      new AlarmConfig(2516, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_27"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_27"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2517), 
      new AlarmConfig(2517, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_28"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_28"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2518), 
      new AlarmConfig(2518, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_29"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_29"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2519), 
      new AlarmConfig(2519, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_30"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_30"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2520), 
      new AlarmConfig(2520, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_31"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_31"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2521), 
      new AlarmConfig(2521, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_32"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_32"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2522), 
      new AlarmConfig(2522, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_33"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_33"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2523), 
      new AlarmConfig(2523, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_34"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_34"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2524), 
      new AlarmConfig(2524, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_35"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_35"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2525), 
      new AlarmConfig(2525, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_36"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_36"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2526), 
      new AlarmConfig(2526, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_37"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_37"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2527), 
      new AlarmConfig(2527, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_38"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_38"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2528), 
      new AlarmConfig(2528, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_39"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_39"), 
      5000L));

    alarmConfig.put(Integer.valueOf(2529), 
      new AlarmConfig(2529, 
      Msg.getString("Alarm.TYPE_ROADM_SWITCHED_PATH_ACTIVE_40"), false, 
      40, 
      Msg.getString("AlarmDescr.TYPE_ROADM_SWITCHED_PATH_ACTIVE_40"), 
      5000L));

    for (Map.Entry<Integer, AlarmConfig> item : alarmConfig.entrySet())
      if (!(item.getKey().equals(item.getValue().getId())))
        throw new Error("o id do indice não coincide com o id do AlarmConfig");
  }

  public boolean isPartOfMany()
  {
    return this.partOfMany;
  }

  public long getIntermitenceTime() {
    return this.intermitenceTime;
  }

  public void setIntermitenceTime(long intermitenceTime) {
    this.intermitenceTime = intermitenceTime;
  }

  public static void main(String[] args) {
    Locale[] arrayOfLocale;
    int j = (arrayOfLocale = new Locale[] { new Locale("pt", "BR"), 
      new Locale("es", "ES"), new Locale("en", "US") }).length; int i = 0;

    for (; i < j; ++i) {
      Locale locale = arrayOfLocale[i];
      Msg.setLocale(locale);
      System.out.print("\n\n\n\n\nCódigo para ");
      System.out.println(locale);
      System.out.print("\n\n\n\n\n");
      TreeMap<Integer,AlarmConfig> list = new TreeMap<Integer, AlarmConfig>();
      initiateAlarmConfig(list);
      for (AlarmConfig config : list.values())
        System.out.println(
          String.format("REPLACE INTO AlarmType VALUES (%1s,'%2s','%3s',%4s,'%5s',%6s);", new Object[] { 
          config.getId(), 
          config.getNome(), config.getMail(), 
          Integer.valueOf(config.getPriority()), config.getDesc(), 
          Long.valueOf(config.getIntermitenceTime()) }));
    }
  }

  public static final class AlarmNameComparator
    implements Comparator<Alarm>, Serializable
  {
    private static final long serialVersionUID = 1L;

    public int compare(Alarm o1, Alarm o2)
    {
      if (o1 == o2) {
        return 0;
      }
      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return 1;
      }
      String o1name = o1.getAlarmName();
      String o2name = o2.getAlarmName();
      if (o1name == o2name) {
        return 0;
      }
      if (o1name == null) {
        return -1;
      }
      if (o2name == null) {
        return 1;
      }
      return o1name.compareTo(o2name);
    }
  }

  public static enum Type  {
    
    GERENCIA(1),
    CONFIGURACAO(2),
    HOUSE_KEEPING(3),
    PROTECAO(4),
    DESEMPENHO(5),
    FALHA(6);

    private int id;

    private Type(int id){
    	this.id = id;
    }
    public int getId() {
      return this.id;
    }

    public static ElementType getType(int id) {
      for (ElementType type : ElementType.values()) {
        if (type.getId() == id) {
          return type;
        }
      }
      return null;
    }
  }
}