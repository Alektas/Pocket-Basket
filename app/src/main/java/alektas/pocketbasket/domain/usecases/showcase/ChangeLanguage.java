package alektas.pocketbasket.domain.usecases.showcase;

import javax.inject.Inject;

import alektas.pocketbasket.domain.ShowcaseRepository;
import alektas.pocketbasket.domain.usecases.UseCase;

public class ChangeLanguage implements UseCase<String, Void> {
    private ShowcaseRepository mRepository;

    @Inject
    public ChangeLanguage(ShowcaseRepository repository) {
        mRepository = repository;
    }

    @Override
    public Void execute(String language) {
        mRepository.changeLanguage(language);
        return null;
    }
}
