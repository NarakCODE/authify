# Contributing to Authify

Thank you for considering contributing to Authify! This document provides guidelines for contributing to the project.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Respect differing viewpoints and experiences

## How to Contribute

### Reporting Bugs

Before creating a bug report:

1. Check if the bug has already been reported in Issues
2. Verify you're using the latest version
3. Check the documentation to ensure it's not expected behavior

When creating a bug report, include:

- Clear, descriptive title
- Steps to reproduce the issue
- Expected behavior vs actual behavior
- Environment details (OS, Java version, database version)
- Relevant logs or error messages
- Screenshots if applicable

### Suggesting Enhancements

Enhancement suggestions are welcome! Include:

- Clear description of the feature
- Use cases and benefits
- Possible implementation approach
- Any potential drawbacks or considerations

### Pull Requests

1. **Fork the repository**

   ```bash
   git clone https://github.com/YOUR_USERNAME/authify.git
   cd authify
   ```

2. **Create a feature branch**

   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Set up development environment**

   ```bash
   cp .env.example .env
   # Edit .env with your local configuration
   ```

4. **Make your changes**

   - Write clean, readable code
   - Follow existing code style
   - Add comments for complex logic
   - Update documentation if needed

5. **Test your changes**

   ```bash
   ./mvnw test
   ./mvnw spring-boot:run
   ```

6. **Commit your changes**

   ```bash
   git add .
   git commit -m "feat: add amazing feature"
   ```

   Use conventional commit messages:

   - `feat:` - New feature
   - `fix:` - Bug fix
   - `docs:` - Documentation changes
   - `style:` - Code style changes (formatting)
   - `refactor:` - Code refactoring
   - `test:` - Adding or updating tests
   - `chore:` - Maintenance tasks

7. **Push to your fork**

   ```bash
   git push origin feature/your-feature-name
   ```

8. **Create a Pull Request**
   - Provide a clear title and description
   - Reference any related issues
   - Explain what changes were made and why
   - Include screenshots for UI changes

## Development Guidelines

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and concise
- Add JavaDoc comments for public methods
- Use Spring Boot best practices

### Security

- Never commit sensitive data (passwords, API keys, tokens)
- Use environment variables for configuration
- Validate all user inputs
- Follow OWASP security guidelines
- Hash passwords with BCrypt
- Use parameterized queries to prevent SQL injection

### Testing

- Write unit tests for new features
- Ensure existing tests pass
- Test edge cases and error conditions
- Aim for meaningful test coverage

### Documentation

- Update README.md if adding new features
- Add comments for complex logic
- Update API documentation for endpoint changes
- Create/update guides for significant features

## Project Structure

```
src/main/java/in/narakcode/authify/
├── config/          # Configuration classes (Security, CORS)
├── controller/      # REST API endpoints
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Exception handlers
├── filter/          # Security filters (JWT)
├── repository/      # Database repositories
├── service/         # Business logic
│   └── impl/        # Service implementations
└── util/            # Utility classes
```

## Areas for Contribution

### High Priority

- [ ] Rate limiting implementation
- [ ] Account lockout after failed attempts
- [ ] Refresh token mechanism
- [ ] Two-factor authentication (2FA)
- [ ] OAuth2 integration (Google, GitHub)
- [ ] API documentation with Swagger/OpenAPI
- [ ] Integration tests
- [ ] Docker Compose setup

### Medium Priority

- [ ] User profile image upload
- [ ] Email template improvements
- [ ] SMS OTP as backup
- [ ] Account deletion endpoint
- [ ] Admin panel endpoints
- [ ] Audit logging
- [ ] Metrics and monitoring

### Low Priority

- [ ] Multiple language support (i18n)
- [ ] Dark mode email templates
- [ ] WebSocket support for real-time notifications
- [ ] GraphQL API alternative

## Questions?

- Open an issue with the `question` label
- Check existing documentation
- Review closed issues for similar questions

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Authify! 🎉
