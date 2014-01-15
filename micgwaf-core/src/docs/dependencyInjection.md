Dependency Injection
====================

These are some notes regarding dependency injection (DI) and micgwaf.

Question one regarding Components and DI is whether components should live inside the DI container or not.
Using normal injection mechanisms, it is not easily possible to let the DI container produce the components, 
as the parent would need to be injected, and it is difficult to tell the DI Container which bean should be inserted.
It still needs to be checked whether factories(Spring) resp. producer methods(CDI) methods can be used to circumvent this problem.

If, instead, the Components live outside the DI container, it should be possible to inject dependencies into the components.
This can be achieved by a postConstruct method which is called by micgwaf after component construction, 
in which the DI container can be called to inject the objects into the component. E.g for Weld(CDI), this would look like

    BeanManager beanManager = CDI.current().getBeanManager();
    AnnotatedType<T> type = beanManager.createAnnotatedType((Class<T>) component.getClass());
    InjectionTarget<T> it = beanManager.createInjectionTarget(type);
    CreationalContext<T> ctx = beanManager.createCreationalContext(null);
    it.inject(component, ctx);  //call initializer methods and perform field injection
    it.postConstruct(component);  //call the @PostConstruct method
    return component;

The problem that remains that components are serialized/deserialized into the session and the dependency injection
needs to take place each time the object is retrieved from the session. For Weld, then the question is whether
@PostConstruct methods should be called again.
