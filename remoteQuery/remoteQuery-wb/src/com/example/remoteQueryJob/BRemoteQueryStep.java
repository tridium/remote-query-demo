/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.example.remoteQueryJob;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.baja.batchJob.BBatchJobService;
import javax.baja.batchJob.driver.BDeviceJobStep;
import javax.baja.batchJob.driver.BDeviceStepDetails;
import javax.baja.batchJob.driver.DeviceNetworkJobOp;
import javax.baja.data.BIDataTable;
import javax.baja.driver.BDevice;
import javax.baja.file.ExportOp;
import javax.baja.job.BJobState;
import javax.baja.naming.BOrd;
import javax.baja.naming.OrdTarget;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.Lexicon;

import com.tridium.file.exporters.BITableToText;
import com.tridium.fox.sys.BFoxSession;
import com.tridium.provisioningNiagara.ProvisioningConnectionUtil;

@NiagaraType
@NiagaraProperty(
  name = "queryString",
  type = "String",
  defaultValue = "select * from control:ControlPoint where status.isFault"
)
public class BRemoteQueryStep
  extends BDeviceJobStep
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.tridium.remoteQueryJob.BRemoteQueryStep(67784961)1.0$ @*/
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

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BRemoteQueryStep.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public BRemoteQueryStep() {}

  @Override
  protected void doRun(BBatchJobService batchJobService, BDeviceStepDetails details,
    BDevice device, DeviceNetworkJobOp deviceNetworkJobOp)
    throws Exception
  {
    details.message("remoteQuery", "RemoteQueryStep.start", new String[] {device.getName()});

    try (ProvisioningConnectionUtil util = new ProvisioningConnectionUtil(device, details))
    {
      BFoxSession session = util.getEngagedFoxSession();
      try
      {
        BObject obj = BOrd.make("station:|slot:/|bql:" + getQueryString()).get(session);
        details.message("remoteQuery", "RemoteQueryStep.resultType", new String[] {device.getName(), obj.getType().toString()});

        // We get back a BDataTable
        if (obj instanceof BIDataTable)
        {
          BIDataTable table = (BIDataTable) obj;
          ExportOp exportOp = new BqlExportOp(table.asObject());
          BITableToText tableExporter = new BITableToText();
          tableExporter.export(exportOp);
          String exportedString = exportOp.getOutputStream().toString();
          details.message("remoteQuery", "RemoteQueryStep.result", new String[] {device.getName(), exportedString});
        }

        details.success();
        details.complete(BJobState.success);
      }
      catch (Exception e)
      {
        details.failed("remoteQuery", "RemoteQueryStep.bqlFailed", new String[] {device.getName()}, e);
        details.complete(BJobState.failed);
      }
    }
    catch (Exception e)
    {
      details.failed("remoteQuery", "RemoteQueryStep.connectionFailed", new String[] {device.getName()}, e);
      details.complete(BJobState.failed);
    }
  }

  @Override
  public String toString(Context cx)
  {
    return lexicon.getText("RemoteQueryStep.display", getQueryString());
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

  public static final Lexicon lexicon = Lexicon.make("remoteQuery");
}
