# Contributing to OpenML

### Want to get involved?

Awesome, we're happy to have you! :star2: :tada: :clap:

:point_right: Check out our contributing guide in
the [OpenML docs](https://docs.openml.org/Contributing/).

---

## Code Quality

This project uses four code quality tools to keep the codebase consistent and catch bugs early.
All tools are installed as Composer **dev** dependencies.

### Setup

```bash
# From the openml_OS/ directory:
cd openml_OS
php composer.phar install
```

### Available commands

| Command              | What it does                                      |
| -------------------- | ------------------------------------------------- |
| `composer run cs:check` | Check code style (PSR-12) — **no changes made** |
| `composer run cs:fix`   | Auto-fix code style issues                      |
| `composer run phpstan`  | Run PHPStan static analysis (level 3)           |
| `composer run psalm`    | Run Psalm static analysis (level 8)             |
| `composer run phpmd`    | Run PHPMD code smell detection                  |

### Before submitting a PR

1. Run `composer run cs:fix` to auto-fix formatting.
2. Run `composer run phpstan` and fix any reported errors.
3. Review (but don't worry about fixing all) Psalm and PHPMD output — these are informational for now.

### Raising the analysis level

PHPStan and Psalm are intentionally set to permissive levels (3 and 8 respectively) to accommodate the existing legacy codebase.
As issues are fixed, the levels can be raised:

- **PHPStan**: edit `.phpstan.neon` → increase `level` (max is 9)
- **Psalm**: edit `psalm.xml` → decrease `errorLevel` (min is 1)

Config files are in the **repo root**:

| File                  | Tool           |
| --------------------- | -------------- |
| `.phpstan.neon`       | PHPStan        |
| `.php-cs-fixer.php`   | PHP-CS-Fixer   |
| `psalm.xml`           | Psalm          |
| `phpmd.xml`           | PHPMD          |

