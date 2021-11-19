package di.uoa.gr.dira.services;

import di.uoa.gr.dira.models.IModel;
import di.uoa.gr.dira.util.mapper.MapperHelper;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<TModel extends IModel<ID>,
        TEntity,
        ID,
        TRepo extends JpaRepository<TEntity, ID>> implements IService<TModel, ID, TRepo> {
    protected final TRepo repository;
    protected final ModelMapper mapper;
    protected final Type modelType;
    protected final Type entityType;

    protected BaseService(TRepo repository, ModelMapper mapper) {
        Type[] genericClassTypes = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        this.repository = repository;
        this.mapper = mapper;
        this.modelType = genericClassTypes[0];
        this.entityType = genericClassTypes[1];
    }

    public TRepo getRepository() {
        return this.repository;
    }

    @Override
    public List<TModel> findAll() {
        List<TEntity> entities = repository.findAll();
        return MapperHelper.mapList(mapper, entities, modelType);
    }

    @Override
    public Optional<TModel> findById(ID id) {
        return repository.findById(id).map(entity -> mapper.<TModel>map(entity, modelType));
    }

    @Override
    public TModel save(TModel model) {
        TEntity entity = mapper.map(model, entityType);
        TEntity saved = repository.save(entity);
        return mapper.map(saved, modelType);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(TModel model) {
        TEntity entity = mapper.map(model, entityType);
        repository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends TModel> models) {
        List<TEntity> entities = new ArrayList<>();
        for (TModel model : models) {
            TEntity entity = mapper.map(model, entityType);
            entities.add(entity);
        }
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
