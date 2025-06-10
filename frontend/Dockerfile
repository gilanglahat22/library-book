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

# Set environment variables
ENV PORT=3002
ENV HOSTNAME="0.0.0.0"
ENV NODE_ENV="development"

# Expose port
EXPOSE 3002

# Start the development server
CMD ["npm", "run", "dev", "--", "--port", "3002", "--hostname", "0.0.0.0"]