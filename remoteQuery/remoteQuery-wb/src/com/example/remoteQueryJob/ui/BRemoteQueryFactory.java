/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.example.remoteQueryJob.ui;

import javax.baja.batchJob.BBatchJob;
import javax.baja.batchJob.BJobStep;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraSingleton;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.provisioningNiagara.ui.BStationStepFactory;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.ui.BDialog;
import javax.baja.ui.BWidget;
import javax.baja.util.Lexicon;

import com.example.remoteQueryJob.BRemoteQueryStep;

import javax.baja.sys.Sys;
import javax.baja.sys.Type;

@NiagaraType(agent = @AgentOn(types={"remoteQuery:RemoteQueryStep"}))
@NiagaraSingleton
public class BRemoteQueryFactory
  extends BStationStepFactory
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.tridium.remoteQueryJob.ui.BRemoteQueryFactory(2582752102)1.0$ @*/
/* Generated Fri Apr 12 08:21:07 EDT 2024 by Slot-o-Matic (c) Tridium, Inc. 2012-2024 */

  public static final BRemoteQueryFactory INSTANCE = new BRemoteQueryFactory();

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BRemoteQueryFactory.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  @Override
  public BJobStep makeStep(BWidget owner, BBatchJob batchJob, BObject jobTarget,
    BObject source, Context context)
    throws Exception
  {
    String s = BDialog.prompt(
      owner,
      lexicon.get("RemoteQueryFactory.displayName"),
      "",
      100
    );
    if (s == null || s.length() == 0)
    {
      return null;
    }
    BRemoteQueryStep step = new BRemoteQueryStep();
    step.setQueryString(s);
    return step;
  }

  public static final Lexicon lexicon = Lexicon.make("remoteQuery");
}
