package org.apache.shindig.social.websockbackend.spi;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.websockbackend.model.IProcessCycle;

public interface IProcessMiningService {

  public Future<IProcessCycle> addProcessCycle(String docType, IProcessCycle cycle, SecurityToken token);

  public Future<Void> addProcessCycle(String docId, String docType, String start, String end,
          List<String> userList, SecurityToken token);
  
  public Future<RestfulCollection<IProcessCycle>> getProcessCycles(String docType,
          CollectionOptions collectionOptions, SecurityToken token);
  
  public Future<Void> deleteProcessCycles(String docType, SecurityToken token);
}
