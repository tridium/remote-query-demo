/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.example.remoteQuery;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.baja.data.BIDataTable;
import javax.baja.file.ExportOp;
import javax.baja.fox.BFoxProxySession;
import javax.baja.naming.BOrd;
import javax.baja.naming.OrdTarget;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Action;
import javax.baja.sys.BObject;
import javax.baja.sys.Clock;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import com.tridium.file.exporters.BITableToText;
import com.tridium.fox.sys.BFoxClientConnection;
import com.tridium.nd.BNiagaraStation;
import com.tridium.util.TimeFormat;

@NiagaraType
@NiagaraProperty(
  name = "queryString",
  type = "String",
  defaultValue = "select * from control:ControlPoint where status.isFault"
)
@NiagaraAction(
  name = "runQuery",
  flags = Flags.ASYNC
)
public class BRemoteQuery
  extends BComponent
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.tridium.remoteQuery.BRemoteQuery(3675769240)1.0$ @*/
/* Generated Fri Apr 12 08:21:07 EDT 2024 by Slot-o-Matic (c) Tridium, Inc. 2012-2024 */

  //region Property "queryString"

  /**
   * Slot for the {@code queryString} property.
   * @see #getQueryString
   * @see #setQueryString
   */
  public static final Property queryString = newProperty(0, "select * from control:ControlPoint where status.isFault", null);

  /**
   * Get the {@code queryString} property.
   * @see #queryString
   */
  public String getQueryString() { return getString(queryString); }

  /**
   * Set the {@code queryString} property.
   * @see #queryString
   */
  public void setQueryString(String v) { setString(queryString, v, null); }

  //endregion Property "queryString"

  //region Action "runQuery"

  /**
   * Slot for the {@code runQuery} action.
   * @see #runQuery()
   */
  public static final Action runQuery = newAction(Flags.ASYNC, null);

  /**
   * Invoke the {@code runQuery} action.
   * @see #runQuery
   */
  public void runQuery() { invoke(runQuery, null, null); }

  //endregion Action "runQuery"

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BRemoteQuery.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public BRemoteQuery() {}

  public void doRunQuery()
  {
    BComponent root = Sys.getStation().getComponentSpace().getRootComponent();
    BComponent networkObject =
      (BComponent) BOrd.make("service:niagaraDriver:NiagaraNetwork").get(root);
    networkObject.lease(2);
    for (BNiagaraStation station : networkObject.getChildren(BNiagaraStation.class))
    {
      // Get the BFoxClientConnection from the station. This connection
      // is just used for the connection info (BHost, port, etc.),
      // not to create an actual connection to the station.
      BFoxClientConnection clientConnection = station.getClientConnection();
      System.out.println(" Connecting to " + station.getRemoteHost() + ":" +
        clientConnection.getPort());

      // Use the connection info from the clientConnection to get the BFoxSession
      // associated with the host / port. BFoxProxySession.make() will return an
      // existing BFoxSession (if there is one). Otherwise a new one is created.
      BFoxProxySession session = BFoxProxySession.make(station.getRemoteHost(),
        clientConnection.getPort(),
        clientConnection.getUseFoxs(),
        clientConnection.getCredentials());

      String interestName = "runBqlQuery" + TimeFormat.format(Clock.time(), "YYYYMMDD_HHmmss");
      try
      {
        // It is possible that a supervisor and a station have different
        // modules installed. If so, it is possible to get an InvalidChannelException
        // when initializing the fox session. This is typically not an issue,
        // since this query will resolve to a fox data channel, which is always
        // installed. If this occurs, a nasty error message is sent to the
        // console, but the fox request will proceed.
        session.engageNoRetry(interestName);

        BObject obj = BOrd.make("station:|slot:/|bql:" + getQueryString()).get(session);
        System.out.println(" result type " + obj.getType());
        System.out.println(obj.toString());

        // A BDataTable is returned
        if (obj instanceof BIDataTable)
        {
          BIDataTable table = (BIDataTable)obj;
          ExportOp exportOp = new BqlExportOp(table.asObject());
          BITableToText tableExporter = new BITableToText();
          tableExporter.export(exportOp);
          String exportedString = exportOp.getOutputStream().toString();
          System.out.println(exportedString);
        }
      }
      catch(Exception e) { } // session.engageNoRetry(interestName) can throw an exception
      finally
      {
        session.disengage(interestName);
      }
    }
  }

  public static class BqlExportOp
    extends ExportOp
  {
    public BqlExportOp(BObject o)
    {
      super(OrdTarget.unmounted(o));
    }

    @Override
    public OutputStream getOutputStream()
    {
      return outputStream;
    }

    private final OutputStream outputStream = new ByteArrayOutputStream();
  }
}
