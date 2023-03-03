package tp1.server.resources;


import util.Files;
import util.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;


public class JavaFiles implements Files {
    private final ConcurrentHashMap<String, String> files = new ConcurrentHashMap<String, String>();

    public JavaFiles() {
    }

    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {

        if (fileId == null || data == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }


        files.put(fileId, fileId + token);
        File f = new File(fileId);

        try {
            f.createNewFile();
            java.nio.file.Files.write(Paths.get(fileId), data);
        } catch (FileNotFoundException e) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        } catch (IOException e) {
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }


        return Result.ok();

    }

    @Override
    public Result<Void> deleteFile(String fileId, String token) {
        if (fileId == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }


        if (files.remove(fileId) == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        try {
            java.nio.file.Files.delete(Paths.get(fileId));
        } catch (IOException e) {
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }

        return Result.ok();

    }

    @Override
    public Result<byte[]> getFile(String fileId, String token) {
        if (fileId == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        String valid = files.get(fileId);

        if (valid == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        try {
            byte[] data = java.nio.file.Files.readAllBytes(Paths.get(fileId));
            return Result.ok(data);
        } catch (FileNotFoundException e) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        } catch (IOException e) {
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }

    }


}






