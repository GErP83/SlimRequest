package com.gerp83.slimrequest.chain;

import com.gerp83.slimrequest.SlimRequest;
import com.gerp83.slimrequest.model.SlimResult;

/**
 * transfers data from request to the next request in the stack
 *
 */
public interface SlimTransfer {

    void gather(SlimResult result) ;
    void pass(SlimRequest slimRequest);

}