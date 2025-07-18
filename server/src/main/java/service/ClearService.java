package service;

import dataaccess.DataAccess;
import service.results.ClearResult;
import service.requests.ClearRequest;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ClearResult clear(ClearRequest request) throws Exception{
        dataAccess.clearUsers();
        dataAccess.clearAuth();
        dataAccess.clearGames();
        return new ClearResult();
    }
}
