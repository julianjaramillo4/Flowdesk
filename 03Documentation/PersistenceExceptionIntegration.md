# Persistence exception integration

Owner: member 2, JSON persistence work.

`exceptions.PersistenceException` extends `RuntimeException`. It provides constructors accepting a message, or a message and its original cause. User-facing messages must be Spanish. Code, documentation and commit messages must be English.

The current commit defines this contract without changing `DataManager` file handling. The persistence owner must integrate it when implementing safe JSON saving and loading.

## Required integration

- Wrap file access and JSON parsing failures in `PersistenceException`, preserving the cause.
- Report the Spanish message at the application boundary; do not show a technical stack trace to the customer.
- Treat missing first-run files separately from unreadable or malformed files.
- Do not replace failed loads with empty data followed by automatic saving.
- Do not print a successful-save message after an operation fails.
- Handle startup, explicit save and shutdown separately. The shutdown hook runs outside console actions.
- Validate deserialized entities before accepting a loaded dataset. Gson can bypass constructors; new registration methods validate their inputs, but existing `setAll` methods remain a persistence integration boundary.

Example exception construction:

```java
throw new PersistenceException("No se pudieron guardar los datos.", exception);
```

## Related APIs

- `DataManager.saveAll()`, `loadAll()`, `saveCashRegister(...)`, `loadCashRegister()`.
- `CashMovement.getAll()` and `setAll(...)` expose the movement dataset for the planned persistence work.
- `ValidationException` covers invalid business data; `InvalidPaymentException` specializes it for payments.
- `ConsoleInput.runAction(...)` catches business validation and persistence failures during menu actions. Startup and shutdown still require explicit handling as described above.

Verify save/load round trips with temporary directories, existing JSON compatibility and malformed-file recovery. Preserve the English enum names stored in JSON and their Spanish display labels.
