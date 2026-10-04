/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        flipkart: {
          blue: '#2874f0',
          darkBlue: '#1a56c4',
          yellow: '#ffe500',
          darkYellow: '#f59e0b',
          green: '#388e3c',
          bg: '#f1f2f4'
        },
        meesho: {
          pink: '#f43397',
          darkPink: '#c2185b'
        }
      }
    },
  },
  plugins: [],
};
