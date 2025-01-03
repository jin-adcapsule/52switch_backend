#!/bin/bash
echo "=== Setting up 52switch project backend ==="

# Backend setup
echo "Setting up backend environment..."
if [ ! -f ".env" ]; then
  cp .env.example .env
  echo "Copied .env.example to .env."
else
  echo ".env already exists. Skipping."
fi


# Install backend dependencies
echo "Installing backend dependencies..."
if command -v mvn &> /dev/null; then
  mvn clean install
else
  echo "Maven is not installed. Please install it and run this script again."
fi


echo "=== Setup complete ==="
