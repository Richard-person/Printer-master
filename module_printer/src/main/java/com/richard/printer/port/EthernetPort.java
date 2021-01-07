package com.richard.printer.port;

import android.os.NetworkOnMainThreadException;

import com.richard.printer.enumerate.ErrorCode;
import com.richard.printer.enumerate.PortType;
import com.richard.printer.model.PortInfo;
import com.richard.printer.model.ReturnMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * author Richard
 * date 2020/8/18 11:06
 * version V1.0
 * description: 网络端口连接相关实现
 */
public class EthernetPort extends PrinterPort {

    private static final int CONNECT_NET_PORT_TIMEOUT = 10000;

    private InetAddress mInetAddress;
    private SocketAddress mSocketAddress;
    private Socket mNetSocket = new Socket();
    private OutputStream mOutput;
    private InputStream mInput;
    private Process process;

    public EthernetPort(PortInfo portInfo) {
        super(portInfo);
        if (portInfo.getPortType() == PortType.Ethernet && portInfo.getEthernetPort() > 0) {
            try {
                this.mInetAddress = Inet4Address.getByName(portInfo.getEthernetIP());
                this.mPortInfo.setParIsOK(true);
            } catch (Exception var4) {
                this.mPortInfo.setParIsOK(false);
            }
        } else {
            this.mPortInfo.setParIsOK(false);
        }

    }

    @Override
    public ReturnMessage openPort() {
        if (!this.mPortInfo.isParIsOK()) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "PortInfo error !\n");
        }

        try {
            this.mSocketAddress = new InetSocketAddress(this.mInetAddress, this.mPortInfo.getEthernetPort());
            this.mNetSocket.connect(this.mSocketAddress, CONNECT_NET_PORT_TIMEOUT);
            if (this.mOutput != null) {
                this.mOutput = null;
            }

            this.mOutput = this.mNetSocket.getOutputStream();
            if (this.mInput != null) {
                this.mInput = null;
            }

            this.mInput = this.mNetSocket.getInputStream();
            this.mIsOpen = true;
        } catch (NetworkOnMainThreadException var2) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, var2.toString());
        } catch (UnknownHostException var3) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, var3.toString());
        } catch (IOException var4) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, var4.toString());
        } catch (Exception var5) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, var5.toString());
        }

        return new ReturnMessage(ErrorCode.OpenPortSuccess, "Open ethernet port success !\n");
    }

    @Override
    public ReturnMessage closePort() {
        try {
            if (this.mOutput != null) {
                this.mOutput.flush();
            }

            if (this.mNetSocket != null) {
                this.mNetSocket.close();
            }

            this.mIsOpen = false;
            this.mOutput = null;
            this.mInput = null;
        } catch (Exception var2) {
            return new ReturnMessage(ErrorCode.ClosePortFailed, var2.toString());
        }

        return new ReturnMessage(ErrorCode.ClosePortSuccess, "Close ethernet port success !\n");
    }

    @Override
    public ReturnMessage write(int data) {
        if (this.mIsOpen && this.mOutput != null && this.mNetSocket.isConnected()) {
            try {
                this.mOutput.write(data);
            } catch (Exception var3) {
                return new ReturnMessage(ErrorCode.WriteDataFailed, var3.toString());
            }

            return new ReturnMessage(ErrorCode.WriteDataSuccess, "Send 1 byte .\n", 1);
        } else {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "Ethernet port was close !\n");
        }
    }

    @Override
    public ReturnMessage write(byte[] data) {
        if (this.mIsOpen && this.mOutput != null && this.mNetSocket.isConnected()) {
            try {
                this.mOutput.write(data);
            } catch (Exception var3) {
                return new ReturnMessage(ErrorCode.WriteDataFailed, var3.toString());
            }

            return new ReturnMessage(ErrorCode.WriteDataSuccess, "Send " + data.length + " bytes .\n", data.length);
        } else {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "Ethernet port was close !\n");
        }
    }

    @Override
    public ReturnMessage write(byte[] data, int offset, int count) {
        if (this.mIsOpen && this.mOutput != null && this.mNetSocket.isConnected()) {
            try {
                this.mOutput.write(data, offset, count);
            } catch (Exception var5) {
                return new ReturnMessage(ErrorCode.WriteDataFailed, var5.toString());
            }

            return new ReturnMessage(ErrorCode.WriteDataSuccess, "Send " + count + " bytes .\n", count);
        } else {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "Ethernet port was close !\n");
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer, int offset, int count) {
        if (this.mIsOpen && this.mInput != null && this.mNetSocket.isConnected()) {
            int readBytes;
            try {
                readBytes = this.mInput.read(buffer, offset, count);
                if (readBytes == -1) {
                    return new ReturnMessage(ErrorCode.ReadDataFailed, "Ethernet port was close !\n");
                }
            } catch (Exception var6) {
                return new ReturnMessage(ErrorCode.ReadDataFailed, var6.toString());
            }

            return new ReturnMessage(ErrorCode.ReadDataSuccess, "Read " + count + " bytes .\n", readBytes);
        } else {
            return new ReturnMessage(ErrorCode.ReadDataFailed, "Ethernet port was close !\n");
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer) {
        return this.read(buffer, 0, buffer.length);
    }

    @Override
    public int read() {
        if (this.mIsOpen && this.mNetSocket.isConnected() && this.mInput != null) {
            try {
                return this.mInput.read();
            } catch (Exception var2) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean portIsOpen() {
        this.mIsOpen = this.pingHost(this.mPortInfo.getEthernetIP());
        if (!this.mIsOpen) {
            this.mIsOpen = this.pingHost(this.mPortInfo.getEthernetIP());
        }

        return this.mIsOpen;
    }


    /**
     * 验证连接是否畅通
     *
     * @param ip ip地址
     * @return 是否畅通
     */
    private boolean pingHost(String ip) {
        boolean result = false;
        BufferedReader bufferedReader = null;

        try {
            Thread.sleep(2000L);
            this.process = Runtime.getRuntime().exec("ping -c 1 -w 5 " + ip);
            InputStream ins = this.process.getInputStream();
            InputStreamReader reader = new InputStreamReader(ins);
            bufferedReader = new BufferedReader(reader);
            Object var6 = null;

            while (bufferedReader.readLine() != null) {
            }

            int status = this.process.waitFor();
            if (status == 0) {
                result = true;
            } else {
                result = false;
            }
        } catch (IOException var18) {
            result = false;
        } catch (InterruptedException var19) {
            result = false;
        } finally {
            if (this.process != null) {
                this.process.destroy();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }

        return result;
    }

}
