/*
 * ao-net-types - Networking-related value types.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-net-types.
 *
 * ao-net-types is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-net-types is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-net-types.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.net;

import com.aoapps.collections.AoCollections;
import java.util.Locale;
import java.util.Map;

/**
 * From <a href="http://www.iana.org/assignments/protocol-numbers">http://www.iana.org/assignments/protocol-numbers</a>
 * on 2017-03-18, with source citing "Last Updated 2016-06-22".
 *
 * @author  AO Industries, Inc.
 */
// Matches src/main/sql/com/aoapps/net/Protocol-type.sql
public enum Protocol {
  HOPOPT(0, "HOPOPT", "IPv6 Hop-by-Hop Option", true),
  ICMP(1, "ICMP", "Internet Control Message"),
  IGMP(2, "IGMP", "Internet Group Management"),
  GGP(3, "GGP", "Gateway-to-Gateway"),
  IPV4(4, "IPv4", "IPv4 encapsulation"),
  ST(5, "ST", "Stream"),
  TCP(6, "TCP", "Transmission Control"),
  CBT(7, "CBT", "CBT"),
  EGP(8, "EGP", "Exterior Gateway Protocol"),
  IGP(9, "IGP", "any private interior gateway\n(used by Cisco for their IGRP)"),
  BBN_RCC_MON(10, "BBN-RCC-MON", "BBN RCC Monitoring"),
  NVP_II(11, "NVP-II", "Network Voice Protocol"),
  PUP(12, "PUP", "PUP"),
  ARGUS(13, "ARGUS (deprecated)", "ARGUS"),
  EMCON(14, "EMCON", "EMCON"),
  XNET(15, "XNET", "Cross Net Debugger"),
  CHAOS(16, "CHAOS", "Chaos"),
  UDP(17, "UDP", "User Datagram"),
  MUX(18, "MUX", "Multiplexing"),
  DCN_MEAS(19, "DCN-MEAS", "DCN Measurement Subsystems"),
  HMP(20, "HMP", "Host Monitoring"),
  PRM(21, "PRM", "Packet Radio Measurement"),
  XNS_IDP(22, "XNS-IDP", "XEROX NS IDP"),
  TRUNK_1(23, "TRUNK-1", "Trunk-1"),
  TRUNK_2(24, "TRUNK-2", "Trunk-2"),
  LEAF_1(25, "LEAF-1", "Leaf-1"),
  LEAF_2(26, "LEAF-2", "Leaf-2"),
  RDP(27, "RDP", "Reliable Data Protocol"),
  IRTP(28, "IRTP", "Internet Reliable Transaction"),
  ISO_TP4(29, "ISO-TP4", "ISO Transport Protocol Class 4"),
  NETBLT(30, "NETBLT", "Bulk Data Transfer Protocol"),
  MFE_NSP(31, "MFE-NSP", "MFE Network Services Protocol"),
  MERIT_INP(32, "MERIT-INP", "MERIT Internodal Protocol"),
  DCCP(33, "DCCP", "Datagram Congestion Control Protocol"),
  _3PC(34, "3PC", "Third Party Connect Protocol"),
  IDPR(35, "IDPR", "Inter-Domain Policy Routing Protocol"),
  XTP(36, "XTP", "XTP"),
  DDP(37, "DDP", "Datagram Delivery Protocol"),
  IDPR_CMTP(38, "IDPR-CMTP", "IDPR Control Message Transport Proto"),
  TP__(39, "TP++", "TP++ Transport Protocol"),
  IL(40, "IL", "IL Transport Protocol"),
  IPV6(41, "IPv6", "IPv6 encapsulation"),
  SDRP(42, "SDRP", "Source Demand Routing Protocol"),
  IPV6_ROUTE(43, "IPv6-Route", "Routing Header for IPv6", true),
  IPV6_FRAG(44, "IPv6-Frag", "Fragment Header for IPv6", true),
  IDRP(45, "IDRP", "Inter-Domain Routing Protocol"),
  RSVP(46, "RSVP", "Reservation Protocol"),
  GRE(47, "GRE", "Generic Routing Encapsulation"),
  DSR(48, "DSR", "Dynamic Source Routing Protocol"),
  BNA(49, "BNA", "BNA"),
  ESP(50, "ESP", "Encap Security Payload", true),
  AH(51, "AH", "Authentication Header", true),
  I_NLSP(52, "I-NLSP", "Integrated Net Layer Security  TUBA"),
  SWIPE(53, "SWIPE (deprecated)", "IP with Encryption"),
  NARP(54, "NARP", "NBMA Address Resolution Protocol"),
  MOBILE(55, "MOBILE", "IP Mobility"),
  TLSP(56, "TLSP", "Transport Layer Security Protocol\nusing Kryptonet key management"),
  SKIP(57, "SKIP", "SKIP"),
  IPV6_ICMP(58, "IPv6-ICMP", "ICMP for IPv6"),
  IPV6_NONXT(59, "IPv6-NoNxt", "No Next Header for IPv6"),
  IPV6_OPTS(60, "IPv6-Opts", "Destination Options for IPv6", true),
  ANY_HOST_INTERNAL(61, "", "any host internal protocol"),
  CFTP(62, "CFTP", "CFTP"),
  ANY_LOCAL_NETWORK(63, "", "any local network"),
  SAT_EXPAK(64, "SAT-EXPAK", "SATNET and Backroom EXPAK"),
  KRYPTOLAN(65, "KRYPTOLAN", "Kryptolan"),
  RVD(66, "RVD", "MIT Remote Virtual Disk Protocol"),
  IPPC(67, "IPPC", "Internet Pluribus Packet Core"),
  ANY_DISTRIBUTED_FILE_SYSTEM(68, "", "any distributed file system"),
  SAT_MON(69, "SAT-MON", "SATNET Monitoring"),
  VISA(70, "VISA", "VISA Protocol"),
  IPCV(71, "IPCV", "Internet Packet Core Utility"),
  CPNX(72, "CPNX", "Computer Protocol Network Executive"),
  CPHB(73, "CPHB", "Computer Protocol Heart Beat"),
  WSN(74, "WSN", "Wang Span Network"),
  PVP(75, "PVP", "Packet Video Protocol"),
  BR_SAT_MON(76, "BR-SAT-MON", "Backroom SATNET Monitoring"),
  SUN_ND(77, "SUN-ND", "SUN ND PROTOCOL-Temporary"),
  WB_MON(78, "WB-MON", "WIDEBAND Monitoring"),
  WB_EXPAK(79, "WB-EXPAK", "WIDEBAND EXPAK"),
  ISO_IP(80, "ISO-IP", "ISO Internet Protocol"),
  VMTP(81, "VMTP", "VMTP"),
  SECURE_VMTP(82, "SECURE-VMTP", "SECURE-VMTP"),
  VINES(83, "VINES", "VINES"),
  TTP(84, "TTP", "Transaction Transport Protocol"),
  IPTM(84, "IPTM", "Internet Protocol Traffic Manager"),
  NSFNET_IGP(85, "NSFNET-IGP", "NSFNET-IGP"),
  DGP(86, "DGP", "Dissimilar Gateway Protocol"),
  TCF(87, "TCF", "TCF"),
  EIGRP(88, "EIGRP", "EIGRP"),
  OSPFIGP(89, "OSPFIGP", "OSPFIGP"),
  SPRITE_RPC(90, "Sprite-RPC", "Sprite RPC Protocol"),
  LARP(91, "LARP", "Locus Address Resolution Protocol"),
  MTP(92, "MTP", "Multicast Transport Protocol"),
  AX_25(93, "AX.25", "AX.25 Frames"),
  IPIP(94, "IPIP", "IP-within-IP Encapsulation Protocol"),
  MICP(95, "MICP (deprecated)", "Mobile Internetworking Control Pro."),
  SCC_SP(96, "SCC-SP", "Semaphore Communications Sec. Pro."),
  ETHERIP(97, "ETHERIP", "Ethernet-within-IP Encapsulation"),
  ENCAP(98, "ENCAP", "Encapsulation Header"),
  ANY_PRIVATE_ENCRYPTION(99, "", "any private encryption scheme"),
  GMTP(100, "GMTP", "GMTP"),
  IFMP(101, "IFMP", "Ipsilon Flow Management Protocol"),
  PNNI(102, "PNNI", "PNNI over IP"),
  PIM(103, "PIM", "Protocol Independent Multicast"),
  ARIS(104, "ARIS", "ARIS"),
  SCPS(105, "SCPS", "SCPS"),
  QNX(106, "QNX", "QNX"),
  A_N(107, "A/N", "Active Networks"),
  IPCOMP(108, "IPComp", "IP Payload Compression Protocol"),
  SNP(109, "SNP", "Sitara Networks Protocol"),
  COMPAQ_PEER(110, "Compaq-Peer", "Compaq Peer Protocol"),
  IPX_IN_IP(111, "IPX-in-IP", "IPX in IP"),
  VRRP(112, "VRRP", "Virtual Router Redundancy Protocol"),
  PGM(113, "PGM", "PGM Reliable Transport Protocol"),
  ANY_0_HOP(114, "", "any 0-hop protocol"),
  L2TP(115, "L2TP", "Layer Two Tunneling Protocol"),
  DDX(116, "DDX", "D-II Data Exchange (DDX)"),
  IATP(117, "IATP", "Interactive Agent Transfer Protocol"),
  STP(118, "STP", "Schedule Transfer Protocol"),
  SRP(119, "SRP", "SpectraLink Radio Protocol"),
  UTI(120, "UTI", "UTI"),
  SMP(121, "SMP", "Simple Message Protocol"),
  SM(122, "SM (deprecated)", "Simple Multicast Protocol"),
  PTP(123, "PTP", "Performance Transparency Protocol"),
  ISIS_OVER_IPV4(124, "ISIS over IPv4", ""),
  FIRE(125, "FIRE", ""),
  CRTP(126, "CRTP", "Combat Radio Transport Protocol"),
  CRUDP(127, "CRUDP", "Combat Radio User Datagram"),
  SSCOPMCE(128, "SSCOPMCE", ""),
  IPLT(129, "IPLT", ""),
  SPS(130, "SPS", "Secure Packet Shield"),
  PIPE(131, "PIPE", "Private IP Encapsulation within IP"),
  SCTP(132, "SCTP", "Stream Control Transmission Protocol"),
  FC(133, "FC", "Fibre Channel"),
  RSVP_E2E_IGNORE(134, "RSVP-E2E-IGNORE", ""),
  MOBILITY_HEADER(135, "Mobility Header", "", true),
  UDPLITE(136, "UDPLite", ""),
  MPLS_IN_IP(137, "MPLS-in-IP", ""),
  MANET(138, "manet", "MANET Protocols"),
  HIP(139, "HIP", "Host Identity Protocol", true),
  SHIM6(140, "Shim6", "Shim6 Protocol", true),
  WESP(141, "WESP", "Wrapped Encapsulating Security Payload"),
  ROHC(142, "ROHC", "Robust Header Compression"),
  UNASSIGNED(-1, "", "Unassigned"),
  EXPERIMENTATION_AND_TESTING_1(253, "", "Use for experimentation and testing", true),
  EXPERIMENTATION_AND_TESTING_2(254, "", "Use for experimentation and testing", true),
  RESERVED(255, "Reserved", "");

  private static final Protocol[] protocolsByDecimal = new Protocol[256];

  static {
    for (Protocol p : values()) {
      // Skip TTP duplicated
      if (p != TTP && p != UNASSIGNED) {
        int decimal = p.getDecimal();
        assert protocolsByDecimal[decimal] == null: "Duplicate value for decimal (" + decimal + "): " + protocolsByDecimal[decimal] + " and " + p;
        protocolsByDecimal[decimal] = p;
      }
    }
    // Fill any still null with unassigned
    for (int i = 0; i <= 255; i++) {
      if (protocolsByDecimal[i] == null) {
        protocolsByDecimal[i] = UNASSIGNED;
      }
    }
  }

  /**
   * Gets a {@link Protocol} given its decimal representation.
   * <p>
   * For the duplicated decimal {@code 84} shared by both {@link #TTP}
   * and {@link #IPTM}, returns {@link #IPTM}.
   * </p>
   * <p>
   * Returns {@link #UNASSIGNED} for any unassigned decimal.
   * </p>
   *
   * @throws IllegalArgumentException when decimal is not between 0 and 255.
   */
  public static Protocol valueOf(short decimal) throws IllegalArgumentException {
    if (decimal < 0) {
      throw new IllegalArgumentException("decimal < 0: " + decimal);
    }
    if (decimal > 255) {
      throw new IllegalArgumentException("decimal > 255: " + decimal);
    }
    return protocolsByDecimal[decimal];
  }

  private static final Map<String, Protocol> protocolsByUpperKeyword;

  static {
    Protocol[] values = values();
    protocolsByUpperKeyword = AoCollections.newHashMap(values.length);
    for (Protocol value : values) {
      String upperKeyword = value.toString().toUpperCase(Locale.ROOT);
      if (protocolsByUpperKeyword.put(upperKeyword, value) != null) {
        throw new AssertionError("Duplicate protocol: " + upperKeyword);
      }
    }
  }

  /**
   * Gets a protocol given its keyword (or name if doesn't have a keyword),
   * case-insensitive in {@link Locale#ROOT}.
   *
   * @return  the protocol or {@code null} if not found
   *
   * @see #toString()
   */
  public static Protocol getProtocolByKeyword(String keyword) throws IllegalArgumentException {
    return protocolsByUpperKeyword.get(keyword.toUpperCase(Locale.ROOT));
  }

  private final short decimal;
  private final String keyword;
  private final String protocol;
  private final boolean ipv6ExtensionHeader;

  private Protocol(
      int decimal,
      String keyword,
      String protocol,
      boolean ipv6ExtensionHeader
  ) {
    this.decimal = (short) decimal;
    this.keyword = keyword;
    this.protocol = protocol;
    this.ipv6ExtensionHeader = ipv6ExtensionHeader;
  }

  private Protocol(
      int decimal,
      String keyword,
      String protocol
  ) {
    this(decimal, keyword, protocol, false);
  }

  /**
   * @return The keyword, if present, or enum name when no keyword.
   *
   * @see #getProtocolByKeyword(java.lang.String)
   */
  @Override
  public String toString() {
    return keyword.isEmpty() ? name() : keyword;
  }

  /**
   * The 8-bit protocol number.  Protocol 84 has two different listings,
   * both TTP and IPTM.  This value cannot be assumed to match the enum
   * {@link #ordinal()}.
   * <p>
   * {@link #UNASSIGNED} is used for all unassigned decimal values and is
   * represented by {@code -1}.
   * </p>
   *
   * @return The 8-bit protocol number or {@code -1} for {@link #UNASSIGNED}.
   */
  public short getDecimal() {
    return decimal;
  }

  /**
   * @return The keyword or {@code ""} when no keyword in table
   *
   * @see #toString()
   */
  public String getKeyword() {
    return keyword;
  }

  /**
   * @return The protocol description or {@code ""} when no protocol in table
   */
  public String getProtocol() {
    return protocol;
  }

  /**
   * @return The IPv6 Extension Header flag.
   */
  public boolean isIpv6ExtensionHeader() {
    return ipv6ExtensionHeader;
  }
}
