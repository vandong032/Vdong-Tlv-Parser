[![maven](https://maven-badges.herokuapp.com/maven-central/com.payneteasy/ber-tlv/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.payneteasy/ber-tlv)


EMV-TLV parser and builder
==========================

BerTlv is a java library for parsing and building BER TLV encoded data.

## Features

* supported types: amount, date, time, text, BCD, bytes
* thread safe (provides immutable container BerTlv)
* production ready (uses in several projects)
* lightweight (no external dependencies)

## Setup with dependency managers

### Maven

```xml
<dependency>
  <groupId>com.vandong</groupId>
  <artifactId>Vdong-Tlv</artifactId>
  <version>1.0-11</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.vandong:Vdong-Tlv:1.0-11'
```

How to parse
------------

```java
byte[] bytes = HexUtil.parseHex("50045649534157131000023100000033D44122011003400000481F");

EmvTlvParser parser = new EmvTlvParser(LOG);
Tlvs tlvs = parser.parse(bytes, 0, bytes.length);
  
EmvTlvLogger.log("    ", tlvs, LOG);
```

How to build
------------

```java
byte[] bytes =  new EmvTlvBuilder()
                .addHex(new BerTag(0x50), "56495341")
                .addHex(new BerTag(0x57), "1000023100000033D44122011003400000481F")
                .buildArray();
```


## License

The BerTlv framework is licensed under the Apache License 2.0
