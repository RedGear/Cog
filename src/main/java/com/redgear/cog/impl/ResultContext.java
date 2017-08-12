package com.redgear.cog.impl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public class ResultContext {

    private final ResultSet resultSet;
    private final String label;

    public ResultContext(ResultSet resultSet, String label) {
        this.resultSet = resultSet;
        this.label = label;
    }

    public String getString() throws SQLException {
        return resultSet.getString(label);
    }

    public String getNString() throws SQLException {
        return resultSet.getNString(label);
    }

    public Array getArray() throws SQLException {
        return resultSet.getArray(label);
    }

    public InputStream getAsciiStream() throws SQLException {
        return resultSet.getAsciiStream(label);
    }

    public BigDecimal getBigDecimal() throws SQLException {
        return resultSet.getBigDecimal(label);
    }

    public InputStream getBinaryStream() throws SQLException {
        return resultSet.getBinaryStream(label);
    }

    public Blob getBlob() throws SQLException {
        return resultSet.getBlob(label);
    }

    public boolean getBoolean() throws SQLException {
        return resultSet.getBoolean(label);
    }

    public byte getByte() throws SQLException {
        return resultSet.getByte(label);
    }

    public byte[] getBytes() throws SQLException {
        return resultSet.getBytes(label);
    }

    public Reader getCharacterStream() throws SQLException {
        return resultSet.getCharacterStream(label);
    }

    public Clob getClob() throws SQLException {
        return resultSet.getClob(label);
    }

    public Date getDate() throws SQLException {
        return resultSet.getDate(label);
    }

    public Date getDate(Calendar calendar) throws SQLException {
        return resultSet.getDate(label, calendar);
    }

    public double getDouble() throws SQLException {
        return resultSet.getDouble(label);
    }

    public float getFloat() throws SQLException {
        return resultSet.getFloat(label);
    }

    public int getInt() throws SQLException {
        return resultSet.getInt(label);
    }

    public long getLong() throws SQLException {
        return resultSet.getLong(label);
    }

    public Ref getRef() throws SQLException {
        return resultSet.getRef(label);
    }

    public short getShort() throws SQLException {
        return resultSet.getShort(label);
    }

    public SQLXML getSQLXML() throws SQLException {
        return resultSet.getSQLXML(label);
    }

    public Time getTime() throws SQLException {
        return resultSet.getTime(label);
    }

    public Time getTime(Calendar calendar) throws SQLException {
        return resultSet.getTime(label, calendar);
    }

    public Timestamp getTimestamp() throws SQLException {
        return resultSet.getTimestamp(label);
    }

    public Timestamp getTimestamp(Calendar calendar) throws SQLException {
        return resultSet.getTimestamp(label, calendar);
    }

    public URL getURL() throws SQLException {
        return resultSet.getURL(label);
    }

    public Reader getNCharacterStream() throws SQLException {
        return resultSet.getNCharacterStream(label);
    }

    public NClob getNClob() throws SQLException {
        return resultSet.getNClob(label);
    }

}
