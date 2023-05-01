package com.hawolt.socket.rms;

import com.hawolt.io.Core;
import com.hawolt.rtmp.utility.Base64GZIP;
import com.hawolt.rtmp.utility.ByteMagic;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created: 01/05/2023 01:50
 * Author: Twitter @hawolt
 **/

public class Test {
    private byte[] buffer = new byte[0];

    public static void main(String[] args) throws IOException {
        String message = "{\"payload\":{\"resource\":\"restrictions/restriction-issued\",\"payload\":\"{\\\"type\\\":\\\"PERMANENT_BAN\\\",\\\"reason\\\":\\\"INAPPROPRIATE_TEXT\\\",\\\"scope\\\":\\\"lol\\\",\\\"vendedRestrictionData\\\":{\\\"expirationMillis\\\":null}}\",\"service\":\"discipline.restriction\",\"version\":\"1.0\",\"timestamp\":1682898335465},\"subject\":\"rms:message\",\"type\":\"push\",\"ts\":1682898335}";
        byte[] payload = Base64GZIP.gzip(message);
        System.err.println(ByteMagic.toHex(payload));
        Test test = new Test();
        test.writeByte((byte) 0b10000010);
        test.writeByte((byte) 0b01111110);
        test.writeByte((byte) ((payload.length >> 8) & 0xFF));
        test.writeByte((byte) ((payload.length) & 0xFF));
        test.writeBytes(payload);
        WebsocketFrame frame = new WebsocketFrame(test.buffer);
        System.out.println(frame.isFinalSegment());
        System.out.println(frame.getRSV1());
        System.out.println(frame.getRSV2());
        System.out.println(frame.getRSV3());
        System.out.println("OP: " + frame.getOpCode());
        System.out.println("MASK: " + frame.isMasked());
        System.out.println(frame.getLengthByte());
        System.out.println(frame.getLengthShort());
        System.out.println(ByteMagic.toHex(test.buffer));
        System.out.println(new String(Base64GZIP.unzip(frame.getPayload())));
        System.out.println("---");
        byte[] data = DatatypeConverter.parseHexBinary("827E00F51F8B08000000000000034D50C16E83300CFD179FBB6E5DD78AE5C6340E3D9421C46107A42A03ABF314481407B40AF1EF73D0A472F37B7ECF7EF6044EDF8CD52DA8093CB21D7C83A062193C35816CCF8F2BF040CC03B6B0B9FB60AA21DC1CD6A06A28B2F29CE6595E5DDED2BC864D2D9334DB7E699EF2B428CA8FA23CA55576A9B2CF6A517063FFDDC69A8519B16FB12DEF6BDF75D0A2904DF8EBC8EBC89DC9186261FBC1987996488C7EA4257E4BDC9033D4E376155E24237A8E9582DDF64970A04EFABA73A076C7E439794DF6FBC3CBF130CBB4E1EB079B109FD1B11219EB2B468BDC2AA41BF83B225E3BE73F4CB62B1250010000");
        System.out.println(data.length);
        //  byte[] data = DatatypeConverter.parseHexBinary("827E04B51F8B0800000000000003ED564B77A24814FE2FAE9B69DE6076BE830A8A8110ECF4C9298A520B0B502844C8C97FEF02CDC44C4F2FBA17B39A9572BFFBAEFB7AEDD0EA803A779D4391EF3A5F3A34EFDC09AA2E767941E5F92F9DBC0822042963C8E2FC2E46790EB688F11D4045521076EE5E3B19CAD32283AD1290518CF2AF27E16B9252BCC110509C263913C85176C22DD3F2C2D418C34C2105F1E1D6A6AAF037EA3BAFCF9D030115CA9E99ADF63FDDA4596C84ECFBB933723DE1B9F385D18B025F485D61233125120720409CC24385037C57E7641E061284A286D04504409816096D3589B22A49AAA40BB22889B2A032382FE2384D50D6E282ACA8B2AE291A0310C15B1C608269750FF21D432549530549E3654DD47889D705496E5CBAC4C9F06FAF7FE4E0AF62FD53C73304C28AD1685620F609416224274CD107A971B9BA9ADBF08AA08912E498629E9301F350970589D3601820C0838DAE5DDD6C841E5196B3976E2C369652825A25F3516F385AB56CB8B515F6AB875BFF92821006C688821050F041A108C41F5F5B1023330DD107E5A2CE79AFA02BF0F6BD09ACC83294D0A6CEAA6BD5FC515CBF48FFE953A897E2FCFF95FFFB57DE017A79DD08FF662C7101A7251DD2F42A5FD2561E55D30C78365E60E36CE012FBDE3931A214BBC4AC2CC7C71BFB2FC6C2AFA586654A426784E783691D7A4623A2989E5F9B435F302B61E78B633277DCD2AF5DEA3B3D6531E07973626153B422330AC9FA221B8189DCE88ACDA17BB66AA3F62BBE326BA39C3BABDDC2D9D2C5D03E9B152FF9E20AAFBD51E97BBE6CD5ADEC014A66239B076249E1645C8513426065E446B252E0C0508DF87C0A0642096397AEC5315D3F302C7E941BCC747CD98CF6A51919A5392831F0C63C0BF56C45DB6AE1F4D86FAF64368EE164DFC42698F134B21C57B006BCB08ED6FBB963CB96E7D28543762CB69AE9E2FDD8E5D78ECB5F5315F89BBDBB19257DD50D16DDF504F0789DD0AA12D08E56E3BEF9609B33A13B9FCB334B3EAD268A9859F1634F232A908DAEED7027D30036E7EAC77E8DF3C729B4236E4486CBDDCB617BBC07A5BC128E60A24491609BB9B829FDE0B4CC4A1A08D3FD319B05F7C193764CADAA96AB7502B7DC5CCD74BB1A53AD968647EEC95B9D0467E66A432F31352D982D377AED770F8B992319EBE3D4D0071C7990B642B867EDB41E2C9FECE58BA9A02459E81BD817CE239166314A568B344BCF80F36AAF2EC98A3FCF603A2EF2C04AB525871F2329E1CD0D8946AC7C95259CAF6443CDE7D998E52D09E8E265E347C5F1A96FF5EE7D749E3FF55E74CD19EF6D526EB7CADCA61324F6732F1F8D88A25B536162A783B2AD5F56FA4982C880001CFF5EE587690C70D2CA909470870C714DD3B51805D916D115DA5E3A9CF543C184DEDEDE9BDF6187424B8724CD51D8CAFCDCB10052CC7AB69AA770DF30DD6D00C9D10DF0C0ECB009E15268624270FEB3E80AB1E1867EC580184C330CDBC3E203BBD9CAFF60F8F6FD33EC812CC1C9F66F2806E776553CE0BA8943F9D715CF097A57EB8A6AB3DCBBA22877559EF10569BB6530C40790D08BC6B776066F31F3A13D7E3E5CFCBC3820AB2C96875E337B6EAE1F516F863B3B9650769B811B0EB999F20169F3FB3E65DF13D1183F16A8B84DD23BC40EAEAB07ECACFAA48F3DF20F627B701E080A0000");
        //byte[] data = DatatypeConverter.parseHexBinary("217E00F51F8B08000000000000004D50C16E83300CFD179FBB6E5DD78AE5C6340E3D9421C46107A42A03ABF314481407B40AF1EF73D0A472F37B7ECF7EF6044EDF8CD52DA8093CB21D7C83A062193C35816CCF8F2BF040CC03B6B0B9FB60AA21DC1CD6A06A28B2F29CE6595E5DDED2BC864D2D9334DB7E699EF2B428CA8FA23CA55576A9B2CF6A517063FFDDC69A8519B16FB12DEF6BDF75D0A2904DF8EBC8EBC89DC9186261FBC1987996488C7EA4257E4BDC9033D4E376155E24237A8E9582DDF64970A04EFABA73A076C7E439794DF6FBC3CBF130CBB4E1EB079B109FD1B11219EB2B468BDC2AA41BF83B225E3BE73F4CB62B1250010000");
        WebsocketFrame t = new WebsocketFrame(data);
        System.out.println(t.isFinalSegment());
        System.out.println(t.getRSV1());
        System.out.println(t.getRSV2());
        System.out.println(t.getRSV3());
        System.out.println("OP: " + t.getOpCode());
        System.out.println("MASK " + t.isMasked());
        System.out.println(t.getLengthByte());
        System.out.println(t.getLengthShort());
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(t.getPayload()))) {
            String b = Core.read(gis).toString();
            System.out.println(b);
            System.out.println(ByteMagic.toHex(Base64GZIP.gzip(b)));
        }


    }

    public void writeByte(byte b) {
        writeBytes(b);
    }

    public void writeBytes(byte... bytes) {
        byte[] b = new byte[buffer.length + bytes.length];
        System.arraycopy(buffer, 0, b, 0, buffer.length);
        System.arraycopy(bytes, 0, b, buffer.length, bytes.length);
        this.buffer = b;
    }
}