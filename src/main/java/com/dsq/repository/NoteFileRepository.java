package com.dsq.repository;

import com.dsq.Note;
import com.dsq.ExportFormat;
import com.dsq.exception.FileOperationException;
import com.dsq.exception.SerializationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 笔记文件仓库类 - 负责笔记数据的文件持久化
 */
public class NoteFileRepository {
    private static final String DEFAULT_DATA_FILE = "notes.dat";
    private static final String BACKUP_EXTENSION = ".backup";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String dataFilePath;

    public NoteFileRepository() {
        this.dataFilePath = DEFAULT_DATA_FILE;
    }

    public NoteFileRepository(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    /**
     * 保存笔记列表到文件
     */
    public void saveNotes(List<Note> notes) throws FileOperationException {
        if (notes == null) {
            throw new FileOperationException("保存笔记", dataFilePath, "笔记列表为空");
        }

        // 创建备份
        createBackup(dataFilePath);

        // 使用try-with-resources确保资源释放
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(dataFilePath))) {
            oos.writeObject(notes);  // 序列化整个笔记列表
            oos.flush();
            System.out.println("笔记数据保存成功：" + dataFilePath + "，共 " + notes.size() + " 条笔记");
        } catch (IOException e) {
            // 保存失败时恢复备份
            restoreBackup(dataFilePath);
            throw new FileOperationException("保存笔记数据", dataFilePath, e);
        }
    }

    /**
     * 从文件加载笔记列表
     */
    @SuppressWarnings("unchecked")
    public List<Note> loadNotes() throws FileOperationException {
        File file = new File(dataFilePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("数据文件不存在或为空，返回空列表");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(dataFilePath))) {
            List<Note> notes = (List<Note>) ois.readObject();  // 反序列化
            System.out.println("笔记数据加载成功，共 " + notes.size() + " 条笔记");
            return notes;
        } catch (IOException | ClassNotFoundException e) {
            throw new FileOperationException("加载笔记数据", dataFilePath, e);
        }
    }

    /**
     * 导出笔记到指定格式
     */
    public void exportNotes(List<Note> notes, String filePath, ExportFormat format)
            throws FileOperationException {
        if (notes == null || filePath == null) {
            throw new FileOperationException("导出笔记", filePath, "参数不能为空");
        }

        try {
            switch (format) {
                case TEXT:
                    exportToTextFile(notes, filePath);
                    break;
                default:
                    throw new FileOperationException("导出笔记", filePath,
                            "不支持的导出格式: " + format);
            }
            System.out.println("笔记导出成功：" + filePath + "，格式：" + format);
        } catch (IOException e) {
            throw new FileOperationException("导出笔记", filePath, e);
        }
    }

    /**
     * 实现文本格式导出逻辑
     */
    private void exportToTextFile(List<Note> notes, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Note note : notes) {
                writer.println("标题：" + note.getTitle());
                writer.println("创建时间：" + note.getCreatedAt().format(DATE_FORMATTER));
                writer.println("最后修改：" + note.getUpdatedAt().format(DATE_FORMATTER));
                writer.println("标签：" + String.join("，", note.getTags()));
                writer.println("内容：");
                writer.println(note.getContent());
                writer.println("--");
                writer.println(); // 空行分隔
            }
        }
    }

    /**
     * 创建备份文件
     */
    private void createBackup(String filename) {
        File originalFile = new File(filename);
        if (originalFile.exists()) {
            File backupFile = new File(filename + BACKUP_EXTENSION);
            try {
                Files.copy(originalFile.toPath(), backupFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println("创建备份文件: " + backupFile.getName());
            } catch (IOException e) {
                System.out.println("备份文件创建失败: " + e.getMessage());
            }
        }
    }

    /**
     * 恢复备份文件
     */
    private void restoreBackup(String filename) {
        File backupFile = new File(filename + BACKUP_EXTENSION);
        File originalFile = new File(filename);

        if (backupFile.exists()) {
            try {
                Files.copy(backupFile.toPath(), originalFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println("从备份恢复文件: " + originalFile.getName());
            } catch (IOException e) {
                System.out.println("备份恢复失败: " + e.getMessage());
            }
        }
    }

    /**
     * 获取数据文件路径
     */
    public String getDataFilePath() {
        return dataFilePath;
    }

    /**
     * 检查数据文件是否存在
     */
    public boolean dataFileExists() {
        return new File(dataFilePath).exists();
    }

    /**
     * 删除数据文件（用于测试清理）
     */
    public void deleteDataFile() {
        File file = new File(dataFilePath);
        if (file.exists()) {
            file.delete();
            System.out.println("删除数据文件: " + dataFilePath);
        }
    }
}