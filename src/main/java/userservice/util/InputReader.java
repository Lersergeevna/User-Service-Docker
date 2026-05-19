package userservice.util;

import userservice.constants.Messages;
import userservice.exception.InputClosedException;
import userservice.exception.InvalidInputException;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Отвечает за чтение и первичную проверку пользовательского ввода.
 */
public final class InputReader implements AutoCloseable {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Считывает непустую строку.
     *
     * @param message приглашение ко вводу
     * @return непустая строка
     */
    public String readNonBlank(String message) {
        while (true) {
            String value = readLine(message).trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println(Messages.formatError(Messages.EMPTY_VALUE));
        }
    }

    /**
     * Считывает и валидирует имя пользователя.
     *
     * @param message приглашение ко вводу
     * @return корректное имя
     */
    public String readName(String message) {
        while (true) {
            String value = readLine(message);
            try {
                return Validators.requireValidName(value);
            } catch (InvalidInputException e) {
                System.out.println(Messages.formatError(e.getMessage()));
            }
        }
    }

    /**
     * Считывает и валидирует e-mail.
     *
     * @param message приглашение ко вводу
     * @return корректный e-mail
     */
    public String readEmail(String message) {
        while (true) {
            String value = readLine(message);
            try {
                return Validators.requireValidEmail(value);
            } catch (InvalidInputException e) {
                System.out.println(Messages.formatError(e.getMessage()));
            }
        }
    }

    /**
     * Считывает и валидирует возраст.
     *
     * @param message приглашение ко вводу
     * @return корректный возраст
     */
    public int readAge(String message) {
        while (true) {
            String raw = readLine(message).trim();
            try {
                int age = Integer.parseInt(raw);
                return Validators.requireValidAge(age);
            } catch (NumberFormatException e) {
                System.out.println(Messages.formatError(Messages.INVALID_INT));
            } catch (InvalidInputException e) {
                System.out.println(Messages.formatError(e.getMessage()));
            }
        }
    }

    /**
     * Считывает и валидирует положительный идентификатор.
     *
     * @param message приглашение ко вводу
     * @return корректный положительный идентификатор
     */
    public long readPositiveId(String message) {
        while (true) {
            String raw = readLine(message).trim();
            try {
                long id = Long.parseLong(raw);
                return Validators.requireValidId(id);
            } catch (NumberFormatException e) {
                System.out.println(Messages.formatError(Messages.INVALID_ID));
            } catch (InvalidInputException e) {
                System.out.println(Messages.formatError(e.getMessage()));
            }
        }
    }

    /**
     * Считывает подтверждение в формате Y/N.
     *
     * @param message приглашение ко вводу
     * @return true, если введено Y, иначе false
     */
    public boolean readConfirmation(String message) {
        while (true) {
            String value = readLine(message).trim();
            if ("Y".equalsIgnoreCase(value)) {
                return true;
            }
            if ("N".equalsIgnoreCase(value)) {
                return false;
            }
            System.out.println(Messages.formatError(Messages.INVALID_CONFIRMATION));
        }
    }

    /**
     * Освобождает ресурсы Scanner.
     */
    @Override
    public void close() {
        scanner.close();
    }

    private String readLine(String message) {
        System.out.print(message);
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException | IllegalStateException e) {
            throw new InputClosedException(Messages.INPUT_CLOSED);
        }
    }
}
