# Use Node.js 18 as base image
FROM node:18

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Environment variables will be supplied from docker-compose.yml
EXPOSE 3000

# Start the development server
CMD ["npm", "run", "dev", "--", "--port", "3000", "--hostname", "0.0.0.0"]