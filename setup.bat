@echo off
echo === Setting up 52switch project backend ===

:: Backend setup
echo Setting up backend environment...
if not exist ".env" (
  copy ".env.example" ".env"
  echo Copied .env.example to .env in 52switch-backend.
) else (
  echo .env already exists in 52switch-backend. Skipping.
)

:: Install backend dependencies
echo Installing backend dependencies...
if exist "mvn.cmd" (
  mvn clean install
) else (
  echo Maven is not installed. Please install it and run this script again.
)

echo === Backend Setup complete ===
