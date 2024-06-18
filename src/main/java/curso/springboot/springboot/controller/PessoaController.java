package curso.springboot.springboot.controller;

import curso.springboot.springboot.model.Pessoa;
import curso.springboot.springboot.model.Telefone;
import curso.springboot.springboot.repository.PessoraRepository;
import curso.springboot.springboot.repository.TelefoneRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PessoaController {

    @Autowired
    private PessoraRepository pessoraRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
    public ModelAndView inicio() {
        ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
        mv.addObject("pessoa", new Pessoa());
        return mv;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/**/salvarpessoa")
    public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
            Iterable<Pessoa> pessoas = pessoraRepository.findAll();
            mv.addObject("pessoas", pessoas);
            mv.addObject("pessoa", pessoa);
            List<String> msg = new ArrayList<String>();
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                msg.add(objectError.getDefaultMessage()); //Vem das anotações no model.
            }
            mv.addObject("msg", msg);
            return mv;
        }

        pessoraRepository.save(pessoa);

        ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
        Iterable<Pessoa> pessoas = pessoraRepository.findAll();
        mv.addObject("pessoas", pessoas);
        mv.addObject("pessoa", new Pessoa());
        return mv;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
    public ModelAndView pessoas() {
        ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
        Iterable<Pessoa> pessoas = pessoraRepository.findAll();
        mv.addObject("pessoas", pessoas);
        mv.addObject("pessoa", new Pessoa());
        return mv;
    }

    @GetMapping("/editarpessoa/{idpessoa}")
    public ModelAndView editar(@PathVariable ("idpessoa") Long idpessoa) {
        Optional<Pessoa> pessoa = pessoraRepository.findById(idpessoa);

        ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
        mv.addObject("pessoa", pessoa.get());
        return mv;
    }

    @GetMapping("/excluirpessoa/{idpessoa}")
    public ModelAndView deletar(@PathVariable ("idpessoa") Long idpessoa) {
        pessoraRepository.deleteById(idpessoa);

        ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
        mv.addObject("pessoas", pessoraRepository.findAll());
        mv.addObject("pessoa", new Pessoa());
        return mv;
    }

    @PostMapping("**/pesquisarnome")
    public ModelAndView pesquisar(@RequestParam ("search") String search) {
        ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
        mv.addObject("pessoas", pessoraRepository.findByNome(search));
        mv.addObject("pessoa", new Pessoa());
        return mv;
    }

    @GetMapping("/telefones/{idpessoa}")
    public ModelAndView telefones(@PathVariable ("idpessoa") Long idpessoa) {
        Optional<Pessoa> pessoa = pessoraRepository.findById(idpessoa);

        ModelAndView mv = new ModelAndView("cadastro/telefones");
        mv.addObject("pessoa", pessoa.get());
        mv.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
        return mv;
    }

    @PostMapping("**/addfonepessoa/{pessoaid}")
    public ModelAndView addfone(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
        Pessoa pessoa = pessoraRepository.findById(pessoaid).get();

        if (telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
            ModelAndView mv = new ModelAndView("cadastro/telefones");
            mv.addObject("pessoa", pessoa);
            mv.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
            List<String> msg = new ArrayList<String>();
            msg.add("Número deve ser informado.");
            mv.addObject("msg", msg);
            return mv;
        }

        telefone.setPessoa(pessoa);
        telefoneRepository.save(telefone);

        ModelAndView mv = new ModelAndView("cadastro/telefones");
        mv.addObject("pessoa", pessoa);
        mv.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
        return mv;
    }

    @GetMapping("/excluirtelefone/{idtelefone}")
    public ModelAndView deltelefone(@PathVariable ("idtelefone") Long idtelefone) {

        Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
        telefoneRepository.deleteById(idtelefone);

        ModelAndView mv = new ModelAndView("cadastro/telefones");
        mv.addObject("pessoa", pessoa);
        mv.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
        return mv;
    }
}
